/**
 * ARIMA模型实现
 * 简化版ARIMA，用于捕捉时间序列的线性趋势
 */

/**
 * 计算一阶差分
 * @param {Array} data - 原始数据
 * @returns {Array} 一阶差分后的数据
 */
function calculateDifferences(data, d = 1) {
  if (d <= 0) return data;
  
  let diff = data;
  for (let i = 0; i < d; i++) {
    const temp = [];
    for (let j = 1; j < diff.length; j++) {
      temp.push(diff[j] - diff[j - 1]);
    }
    diff = temp;
  }
  
  return diff;
}

/**
 * 计算自相关函数(ACF)
 * @param {Array} data - 时间序列数据
 * @param {number} maxLag - 最大滞后阶数
 * @returns {Array} ACF值数组
 */
function calculateACF(data, maxLag) {
  const n = data.length;
  const mean = data.reduce((sum, val) => sum + val, 0) / n;
  const variance = data.reduce((sum, val) => sum + Math.pow(val - mean, 2), 0) / n;
  
  const acf = [];
  
  for (let lag = 0; lag <= maxLag; lag++) {
    let covariance = 0;
    for (let t = lag; t < n; t++) {
      covariance += (data[t] - mean) * (data[t - lag] - mean);
    }
    covariance /= n;
    acf.push(covariance / variance);
  }
  
  return acf;
}

/**
 * 计算偏自相关函数(PACF)
 * @param {Array} data - 时间序列数据
 * @param {number} maxLag - 最大滞后阶数
 * @returns {Array} PACF值数组
 */
function calculatePACF(data, maxLag) {
  const acf = calculateACF(data, maxLag);
  const pacf = new Array(maxLag + 1).fill(0);
  
  pacf[0] = 1;
  
  // 使用Yule-Walker方程计算PACF
  for (let k = 1; k <= maxLag; k++) {
    let sum1 = 0;
    let sum2 = 0;
    
    for (let j = 1; j < k; j++) {
      sum1 += pacf[j] * acf[k - j];
    }
    
    for (let j = 0; j < k; j++) {
      sum2 += pacf[j] * acf[j];
    }
    
    pacf[k] = (acf[k] - sum1) / (1 - sum2);
  }
  
  return pacf;
}

/**
 * 简单ARIMA预测实现
 * @param {Array} data - 时间序列数据
 * @param {Object} params - ARIMA参数 {p, d, q}
 * @param {number} steps - 预测步数
 * @returns {Array} 预测结果
 */
export function calculateArima(data, params = { p: 1, d: 1, q: 1 }, steps = 1) {
  const { p, d, q } = params;
  
  // 1. 数据差分处理
  const diffData = calculateDifferences(data, d);
  
  // 2. 计算ACF和PACF来确定模型参数
  const maxLag = Math.max(p, q) + 5;
  const acf = calculateACF(diffData, maxLag);
  const pacf = calculatePACF(diffData, maxLag);
  
  // 3. 简化的ARIMA预测（使用移动平均和自回归）
  const predictions = [];
  let currentData = [...diffData];
  
  for (let step = 0; step < steps; step++) {
    // 简单的加权平均预测（简化版，实际应该使用更复杂的ARIMA参数估计）
    const recentData = currentData.slice(-Math.max(p, q, 3));
    const avg = recentData.reduce((sum, val) => sum + val, 0) / recentData.length;
    
    predictions.push(avg);
    currentData.push(avg);
  }
  
  // 4. 还原差分（反差分）
  let finalPredictions = [...predictions];
  for (let i = 0; i < d; i++) {
    const lastActual = data[data.length - 1 - i];
    finalPredictions = finalPredictions.map(pred => pred + lastActual);
  }
  
  return {
    predictions: finalPredictions,
    diffData,
    acf,
    pacf,
    params
  };
}

/**
 * 自动选择ARIMA参数
 * @param {Array} data - 时间序列数据
 * @returns {Object} 最佳ARIMA参数 {p, d, q}
 */
export function autoSelectArimaParams(data) {
  // 简化的参数选择：基于数据特征选择默认参数
  // 实际应用中应该使用AIC/BIC准则和网格搜索
  const n = data.length;
  
  // 根据数据长度选择合适的参数
  const p = Math.min(3, Math.floor(n / 30));
  const d = 1; // 通常一阶差分就足够
  const q = Math.min(3, Math.floor(n / 30));
  
  return { p, d, q };
}
