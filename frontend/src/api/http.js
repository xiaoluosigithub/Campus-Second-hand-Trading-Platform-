import axios from 'axios'
// no UI imports here

const http = axios.create({
  baseURL: '/api',
  withCredentials: true,
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  if (!(config.data instanceof FormData)) {
    config.headers['Content-Type'] = 'application/json'
  } else if (config.headers && config.headers['Content-Type']) {
    delete config.headers['Content-Type']
  }
  config.withCredentials = true
  return config
})

http.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data && typeof data.code !== 'undefined') {
      if (data.code !== 200) {
        const err = new Error(data.message || '请求失败')
        // @ts-ignore
        err.code = data.code
        return Promise.reject(err)
      }
      return data
    }
    return res
  },
  (error) => {
    const status = error?.response?.status
    let msg = error?.response?.data?.message || error.message || '网络异常'
    const raw = error?.response?.data
    if (!msg && typeof raw === 'string') {
      try {
        const parsed = JSON.parse(raw)
        if (parsed && parsed.message) msg = parsed.message
      } catch (e) {
        msg = raw
      }
      if (!msg) msg = raw
    }
    const err = new Error(msg)
    // @ts-ignore
    err.code = status || -1
    if (status === 401) {
      // 静默，由路由守卫处理受保护页跳转
      return Promise.reject(err)
    }
    return Promise.reject(err)
  }
)

export default http
