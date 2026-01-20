/**
 * ARIMA-LSTM混合模型
 * 融合ARIMA的线性建模能力和LSTM的非线性建模能力
 */

import * as tf from '@tensorflow/tfjs';
import { calculateArima, autoSelectArimaParams } from './arima';
import { createLSTMModel, trainLSTMModel, predictLSTMModel } from './lstm';
import { preprocessData, normalizeData, denormalizeData, createWindowedData } from './data-processor';

/**
 * 训练ARIMA-LSTM混合模型
 * @param {Array} data - 原始时间序列数据
 * @param {Object} options - 训练选项
 * @returns {Promise<Object>} 训练好的混合模型
 */
export async function trainHybridModel(data, options = {}) {
  const {
    windowSize = 7,
    testSplit = 0.2,
    arimaParams = null,
    lstmEpochs = 50,
    lstmBatchSize = 32
  } = options;
  
  // 1. 数据预处理
  const preprocessed = preprocessData(data, { windowSize, testSplit });
  const { cleanedData, trainData, testData } = preprocessed;
  
  // 2. 数据归一化
  const { normalizedData, min, max, range } = normalizeData(cleanedData);
  const normParams = { min, max, range };
  
  // 3. 自动选择ARIMA参数（如果未提供）
  const finalArimaParams = arimaParams || autoSelectArimaParams(trainData);
  
  // 4. 创建LSTM模型
  const lstmModel = createLSTMModel(windowSize);
  
  // 5. 准备LSTM训练数据
  const windowedData = createWindowedData(normalizedData, windowSize);
  const { X, y } = windowedData;
  
  // 6. 训练LSTM模型
  const lstmResult = await trainLSTMModel(lstmModel, X, y, {
    epochs: lstmEpochs,
    batchSize: lstmBatchSize,
    validationSplit: 0.1,
    verbose: 0
  });
  
  // 7. 使用ARIMA模型进行初步预测（作为LSTM的补充）
  const arimaResult = calculateArima(trainData, finalArimaParams, testData.length);
  
  // 8. 评估混合模型性能
  const testWindowed = createWindowedData(
    normalizedData.slice(normalizedData.length - testData.length - windowSize),
    windowSize
  );
  const lstmTestPredictions = await predictLSTMModel(lstmResult.model, testWindowed.X);
  const denormalizedLSTMPreds = denormalizeData(lstmTestPredictions, normParams);
  
  // 9. 融合ARIMA和LSTM的预测结果
  const hybridPredictions = [];
  const alpha = 0.5; // 融合权重，可根据实际情况调整
  
  for (let i = 0; i < Math.min(denormalizedLSTMPreds.length, arimaResult.predictions.length); i++) {
    const hybridPred = alpha * denormalizedLSTMPreds[i] + (1 - alpha) * arimaResult.predictions[i];
    hybridPredictions.push(hybridPred);
  }
  
  // 10. 计算模型评估指标
  const metrics = calculateModelMetrics(testData.slice(0, hybridPredictions.length), hybridPredictions);
  
  return {
    model: {
      lstmModel: lstmResult.model,
      arimaParams: finalArimaParams,
      normParams
    },
    training: {
      preprocessed,
      lstmResult,
      arimaResult
    },
    evaluation: {
      metrics,
      testPredictions: {
        lstm: denormalizedLSTMPreds,
        arima: arimaResult.predictions,
        hybrid: hybridPredictions
      },
      actual: testData.slice(0, hybridPredictions.length)
    },
    options
  };
}

/**
 * 使用ARIMA-LSTM混合模型进行预测
 * @param {Object} hybridModel - 训练好的混合模型
 * @param {Array} data - 输入数据
 * @param {number} steps - 预测步数
 * @returns {Promise<Object>} 预测结果
 */
export async function predictHybridModel(hybridModel, data, steps = 1) {
  const { model: { lstmModel, arimaParams, normParams }, options } = hybridModel;
  const { windowSize = 7 } = options;
  
  // 1. 数据预处理和归一化
  const preprocessed = preprocessData(data, { windowSize, testSplit: 0 });
  const { normalizedData } = normalizeData(preprocessed.cleanedData);
  
  // 2. 使用LSTM模型进行预测
  const lstmPredictions = [];
  let currentWindow = normalizedData.slice(-windowSize);
  
  for (let i = 0; i < steps; i++) {
    // 准备LSTM输入
    const lstmInput = [currentWindow.map(val => [val])];
    
    // LSTM预测
    const lstmPred = await predictLSTMModel(lstmModel, lstmInput);
    const denormalizedLSTMPred = denormalizeData(lstmPred[0], normParams);
    
    lstmPredictions.push(denormalizedLSTMPred);
    
    // 更新窗口
    currentWindow = [...currentWindow.slice(1), denormalizeData(lstmPred[0], normParams)];
  }
  
  // 3. 使用ARIMA模型进行预测
  const arimaResult = calculateArima(data, arimaParams, steps);
  
  // 4. 融合预测结果
  const hybridPredictions = [];
  const alpha = 0.5; // 融合权重
  
  for (let i = 0; i < steps; i++) {
    const hybridPred = alpha * lstmPredictions[i] + (1 - alpha) * arimaResult.predictions[i];
    hybridPredictions.push(hybridPred);
  }
  
  return {
    predictions: {
      lstm: lstmPredictions,
      arima: arimaResult.predictions,
      hybrid: hybridPredictions
    },
    steps,
    modelInfo: {
      windowSize,
      arimaParams,
      normParams
    }
  };
}

/**
 * 计算模型评估指标
 * @param {Array} actual - 实际值
 * @param {Array} predicted - 预测值
 * @returns {Object} 评估指标
 */
function calculateModelMetrics(actual, predicted) {
  const n = actual.length;
  
  // 计算MAE（平均绝对误差）
  const mae = actual.reduce((sum, val, i) => sum + Math.abs(val - predicted[i]), 0) / n;
  
  // 计算MSE（均方误差）
  const mse = actual.reduce((sum, val, i) => sum + Math.pow(val - predicted[i], 2), 0) / n;
  
  // 计算RMSE（均方根误差）
  const rmse = Math.sqrt(mse);
  
  // 计算MAPE（平均绝对百分比误差）
  const mape = actual.reduce((sum, val, i) => {
    const percentError = Math.abs((val - predicted[i]) / val);
    return sum + percentError;
  }, 0) / n * 100;
  
  // 计算R²（决定系数）
  const actualMean = actual.reduce((sum, val) => sum + val, 0) / n;
  const ssRes = actual.reduce((sum, val, i) => sum + Math.pow(val - predicted[i], 2), 0);
  const ssTot = actual.reduce((sum, val) => sum + Math.pow(val - actualMean, 2), 0);
  const r2 = 1 - (ssRes / ssTot);
  
  return {
    mae,
    mse,
    rmse,
    mape,
    r2,
    samples: n
  };
}

/**
 * 生成模拟数据（用于测试）
 * @param {number} length - 数据长度
 * @param {Object} options - 数据生成选项
 * @returns {Array} 模拟时间序列数据
 */
export function generateMockData(length = 100, options = {}) {
  const { trend = 0.1, seasonality = 20, noise = 0.1 } = options;
  const data = [];
  
  for (let i = 0; i < length; i++) {
    // 趋势项
    const trendComponent = trend * i;
    
    // 季节性项
    const seasonalComponent = Math.sin(2 * Math.PI * i / seasonality) * 10;
    
    // 噪声项
    const noiseComponent = (Math.random() - 0.5) * noise * 20;
    
    // 组合所有成分
    const value = trendComponent + seasonalComponent + noiseComponent;
    data.push(value);
  }
  
  return data;
}
