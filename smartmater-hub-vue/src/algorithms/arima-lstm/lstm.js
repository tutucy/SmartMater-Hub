/**
 * LSTM模型实现
 * 使用TensorFlow.js构建LSTM模型，用于捕捉时间序列的非线性模式
 */

import * as tf from '@tensorflow/tfjs';

/**
 * 创建LSTM模型
 * @param {number} windowSize - 输入窗口大小
 * @param {number} numFeatures - 特征数量
 * @returns {tf.Sequential} LSTM模型
 */
export function createLSTMModel(windowSize, numFeatures = 1) {
  const model = tf.sequential();
  
  const lstmUnits = windowSize > 30 ? 32 : 64;
  
  model.add(tf.layers.lstm({
    units: lstmUnits,
    returnSequences: true,
    inputShape: [windowSize, numFeatures],
    kernelInitializer: 'glorotUniform',
    recurrentInitializer: 'glorotUniform',
    biasInitializer: 'zeros',
    dropout: 0.2,
    recurrentDropout: 0.2
  }));
  
  model.add(tf.layers.lstm({
    units: Math.floor(lstmUnits / 2),
    returnSequences: false,
    kernelInitializer: 'glorotUniform',
    recurrentInitializer: 'glorotUniform',
    biasInitializer: 'zeros',
    dropout: 0.2,
    recurrentDropout: 0.2
  }));
  
  model.add(tf.layers.dense({
    units: 16,
    activation: 'relu',
    kernelInitializer: 'glorotUniform'
  }));
  
  model.add(tf.layers.dense({
    units: 1,
    activation: 'linear',
    kernelInitializer: 'glorotUniform'
  }));
  
  model.compile({
    optimizer: tf.train.adam(0.001),
    loss: 'meanSquaredError',
    metrics: ['mae']
  });
  
  return model;
}

/**
 * 训练LSTM模型
 * @param {tf.Sequential} model - LSTM模型
 * @param {Array} X - 输入特征 [samples, time steps, features]
 * @param {Array} y - 目标值 [samples, 1]
 * @param {Object} options - 训练选项
 * @returns {Promise<Object>} 训练结果
 */
export async function trainLSTMModel(model, X, y, options = {}) {
  const {
    epochs = 50,
    batchSize = 32,
    validationSplit = 0.1,
    verbose = 0
  } = options;
  
  // 转换数据为TensorFlow张量
  const xs = tf.tensor3d(X);
  const ys = tf.tensor2d(y, [y.length, 1]);
  
  // 训练模型
  const history = await model.fit(xs, ys, {
    epochs,
    batchSize,
    validationSplit,
    verbose
  });
  
  // 释放张量内存
  xs.dispose();
  ys.dispose();
  
  return {
    model,
    history: history.history,
    epochs: history.epoch.length
  };
}

/**
 * 使用LSTM模型进行预测
 * @param {tf.Sequential} model - 训练好的LSTM模型
 * @param {Array} X - 输入特征 [samples, time steps, features]
 * @returns {Array} 预测结果
 */
export async function predictLSTMModel(model, X) {
  // 转换数据为TensorFlow张量
  const xs = tf.tensor3d(X);
  
  // 进行预测
  const predictions = await model.predict(xs);
  
  // 转换预测结果为JavaScript数组
  const result = predictions.arraySync().flat();
  
  // 释放张量内存
  xs.dispose();
  predictions.dispose();
  
  return result;
}

/**
 * 保存LSTM模型
 * @param {tf.Sequential} model - LSTM模型
 * @param {string} path - 保存路径
 * @returns {Promise<void>}
 */
export async function saveLSTMModel(model, path) {
  await model.save(`localstorage://${path}`);
}

/**
 * 加载LSTM模型
 * @param {string} path - 模型路径
 * @returns {Promise<tf.Sequential>} 加载的模型
 */
export async function loadLSTMModel(path) {
  return await tf.loadLayersModel(`localstorage://${path}`);
}

/**
 * 模型评估
 * @param {tf.Sequential} model - LSTM模型
 * @param {Array} X - 输入特征
 * @param {Array} y - 目标值
 * @returns {Promise<Object>} 评估结果
 */
export async function evaluateLSTMModel(model, X, y) {
  const xs = tf.tensor3d(X);
  const ys = tf.tensor2d(y, [y.length, 1]);
  
  const result = await model.evaluate(xs, ys, {
    batchSize: 32,
    verbose: 0
  });
  
  const metrics = {
    loss: result[0].dataSync()[0],
    mae: result[1].dataSync()[0]
  };
  
  xs.dispose();
  ys.dispose();
  result.forEach(tensor => tensor.dispose());
  
  return metrics;
}
