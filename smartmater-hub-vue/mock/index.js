// 引入mockjs
import Mock from 'mockjs'

// 用户列表数据
const userList = Mock.mock({
  'list|100': [{
    'id|+1': 1,
    'name': '@cname',
    'email': '@email',
    'phone': /^1[3-9]\d{9}$/,
    'role': '@pick([\'admin\', \'user\', \'guest\'])',
    'status': '@boolean',
    'createTime': '@datetime(\'yyyy-MM-dd HH:mm:ss\')'
  }]
})

// 商品列表数据
const productList = Mock.mock({
  'list|50': [{
    'id|+1': 1,
    'name': '@cword(5, 10)',
    'price': '@float(10, 1000, 2, 2)',
    'stock': '@integer(0, 1000)',
    'category': '@pick([\'电子产品\', \'服装\', \'食品\', \'家居\'])',
    'status': '@boolean',
    'createTime': '@datetime(\'yyyy-MM-dd HH:mm:ss\')'
  }]
})

// 物资列表数据（针对智能物资管理系统）
const materialList = Mock.mock({
  'list|100': [{
    'id|+1': 1,
    'code': '@string(\'upper\', 6, 10)',
    'name': '@cword(5, 15)',
    'type': '@pick([\'原材料\', \'半成品\', \'成品\', \'工具\', \'设备\'])',
    'spec': '@cword(3, 8)',
    'unit': '@pick([\'个\', \'件\', \'吨\', \'千克\', \'米\', \'套\'])',
    'stock|0-5000': 100,
    'location': '@cword(3, 10)',
    'status': '@pick([\'在用\', \'闲置\', \'维修\', \'报废\'])',
    'lastCheckTime': '@datetime(\'yyyy-MM-dd\')',
    'createTime': '@datetime(\'yyyy-MM-dd HH:mm:ss\')'
  }]
})

// 导出mock数据配置
export default [
  // 获取用户列表
  {
    url: '/api/users',
    method: 'get',
    response: (config) => {
      const { page = 1, pageSize = 10 } = config.query
      const start = (page - 1) * pageSize
      const end = start + pageSize
      return {
        code: 200,
        message: 'success',
        data: {
          list: userList.list.slice(start, end),
          total: userList.list.length,
          page: parseInt(page),
          pageSize: parseInt(pageSize)
        }
      }
    }
  },

  // 获取商品列表
  {
    url: '/api/products',
    method: 'get',
    response: (config) => {
      const { page = 1, pageSize = 10 } = config.query
      const start = (page - 1) * pageSize
      const end = start + pageSize
      return {
        code: 200,
        message: 'success',
        data: {
          list: productList.list.slice(start, end),
          total: productList.list.length,
          page: parseInt(page),
          pageSize: parseInt(pageSize)
        }
      }
    }
  },

  // 获取物资列表
  {
    url: '/api/materials',
    method: 'get',
    response: (config) => {
      const { page = 1, pageSize = 10, keyword } = config.query
      let filteredList = materialList.list
      
      // 关键词搜索
      if (keyword) {
        filteredList = filteredList.filter(item => 
          item.name.includes(keyword) || 
          item.code.includes(keyword) ||
          item.spec.includes(keyword)
        )
      }
      
      const start = (page - 1) * pageSize
      const end = start + pageSize
      
      return {
        code: 200,
        message: 'success',
        data: {
          list: filteredList.slice(start, end),
          total: filteredList.length,
          page: parseInt(page),
          pageSize: parseInt(pageSize)
        }
      }
    }
  },

  // 获取物资详情
  {
    url: '/api/materials/:id',
    method: 'get',
    response: (config) => {
      const { id } = config.params
      const material = materialList.list.find(item => item.id === parseInt(id))
      if (material) {
        return {
          code: 200,
          message: 'success',
          data: material
        }
      } else {
        return {
          code: 404,
          message: '物资不存在',
          data: null
        }
      }
    }
  },

  // 添加物资
  {
    url: '/api/materials',
    method: 'post',
    response: (config) => {
      const newMaterial = {
        id: materialList.list.length + 1,
        ...config.body,
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      materialList.list.push(newMaterial)
      return {
        code: 200,
        message: '添加成功',
        data: newMaterial
      }
    }
  },

  // 更新物资
  {
    url: '/api/materials/:id',
    method: 'put',
    response: (config) => {
      const { id } = config.params
      const index = materialList.list.findIndex(item => item.id === parseInt(id))
      if (index !== -1) {
        materialList.list[index] = {
          ...materialList.list[index],
          ...config.body
        }
        return {
          code: 200,
          message: '更新成功',
          data: materialList.list[index]
        }
      } else {
        return {
          code: 404,
          message: '物资不存在',
          data: null
        }
      }
    }
  },

  // 删除物资
  {
    url: '/api/materials/:id',
    method: 'delete',
    response: (config) => {
      const { id } = config.params
      const index = materialList.list.findIndex(item => item.id === parseInt(id))
      if (index !== -1) {
        materialList.list.splice(index, 1)
        return {
          code: 200,
          message: '删除成功',
          data: null
        }
      } else {
        return {
          code: 404,
          message: '物资不存在',
          data: null
        }
      }
    }
  },

  // 登录接口
  {
    url: '/api/login',
    method: 'post',
    response: (config) => {
      const { username, password } = config.body
      // 简单验证，实际项目中应该有更复杂的验证逻辑
      if (username && password) {
        return {
          code: 200,
          message: '登录成功',
          data: {
            token: Mock.Random.string(32),
            userInfo: {
              id: 1,
              username: username,
              name: Mock.Random.cname(),
              role: 'admin'
            }
          }
        }
      } else {
        return {
          code: 401,
          message: '用户名或密码错误',
          data: null
        }
      }
    }
  }
]
