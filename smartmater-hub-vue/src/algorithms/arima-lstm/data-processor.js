/**
 * 数据处理器 - 负责数据预处理、归一化和反归一化
 */

/**
 * 数据预处理
 * @param {Array} data - 原始时间序列数据
 * @param {Object} options - 预处理选项
 * @returns {Object} 预处理后的数据和配置
 */
export function preprocessData(data, options = {}) {
  const { 
    windowSize = 7, 
    testSplit = 0.2 
  } = options;

  // 确保数据为数组且不为空
  if (!Array.isArray(data) || data.length === 0) {
    throw new Error('Invalid data format. Expected non-empty array.');
  }

  // 数据清洗：移除NaN和无穷大值
  const cleanedData = data.filter(val => typeof val === 'number' && isFinite(val));
  
  // 计算训练集和测试集分割点
  const splitIndex = Math.floor(cleanedData.length * (1 - testSplit));
  const trainData = cleanedData.slice(0, splitIndex);
  const testData = cleanedData.slice(splitIndex);

  return {
    cleanedData,
    trainData,
    testData,
    windowSize,
    testSplit,
    splitIndex
  };
}

/**
 * 数据归一化
 * @param {Array} data - 原始数据
 * @returns {Object} 归一化后的数据和归一化参数
 */
export function normalizeData(data) {
  const min = Math.min(...data);
  const max = Math.max(...data);
  const range = max - min;
  
  // 处理range为0的情况（所有值相同）
  if (range === 0) {
    return {
      normalizedData: data.map(() => 0),
      min,
      max,
      range
    };
  }
  
  const normalizedData = data.map(val => (val - min) / range);
  
  return {
    normalizedData,
    min,
    max,
    range
  };
}

/**
 * 数据反归一化
 * @param {Array|number} normalizedData - 归一化后的数据
 * @param {Object} normParams - 归一化参数 {min, max, range}
 * @returns {Array|number} 反归一化后的数据
 */
export function denormalizeData(normalizedData, normParams) {
  const { min, max, range } = normParams;
  
  if (range === 0) {
    return Array.isArray(normalizedData) 
      ? normalizedData.map(() => min) 
      : min;
  }
  
  if (Array.isArray(normalizedData)) {
    return normalizedData.map(val => val * range + min);
  } else {
    return normalizedData * range + min;
  }
}

/**
 * 创建时间序列窗口数据
 * @param {Array} data - 归一化后的数据
 * @param {number} windowSize - 窗口大小
 * @returns {Object} 输入特征和目标值
 */
export function createWindowedData(data, windowSize) {
  const X = [];
  const y = [];
  
  for (let i = 0; i < data.length - windowSize; i++) {
    X.push(data.slice(i, i + windowSize));
    y.push(data[i + windowSize]);
  }
  
  return {
    X: X.map(x => x.map(val => [val])), // 转换为适合LSTM的形状 [samples, time steps, features]
    y
  };
}

/**
 * 计算移动平均值
 * @param {Array} data - 时间序列数据
 * @param {number} windowSize - 移动窗口大小
 * @returns {Array} 移动平均值数组
 */
export function calculateMA(data, windowSize) {
  const ma = [];
  
  for (let i = windowSize - 1; i < data.length; i++) {
    const window = data.slice(i - windowSize + 1, i + 1);
    const avg = window.reduce((sum, val) => sum + val, 0) / windowSize;
    ma.push(avg);
  }
  
  return ma;
}
