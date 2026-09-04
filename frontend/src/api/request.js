import axios from 'axios'
import { ElMessage } from 'element-plus'
import { mockHandle } from '@/mock'
import { useUserStore } from '@/store/user'
import router from '@/router'

const useMock = import.meta.env.VITE_USE_MOCK !== 'false'

const instance = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截：附带 token
instance.interceptors.request.use((config) => {
  const user = useUserStore()
  if (user.token) {
    config.headers.Authorization = `Bearer ${user.token}`
  }
  return config
})

// 响应拦截：统一解包 Result
instance.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code !== 0) {
        ElMessage.error(body.msg || '请求失败')
        return Promise.reject(body)
      }
      return body.data
    }
    return body
  },
  (err) => {
    if (err.response?.status === 401) {
      useUserStore().logout()
      router.push('/login')
    }
    ElMessage.error(err.response?.data?.msg || err.message || '网络错误')
    return Promise.reject(err)
  }
)

/**
 * 统一请求封装：mock 阶段走本地 mock，否则走 axios。
 * 返回已解包的 data（Promise）。
 */
export default function request({ url, method = 'get', data, params }) {
  if (useMock) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const body = mockHandle(method.toUpperCase(), url, params, data)
        if (body.code === 0) {
          resolve(body.data)
        } else {
          reject(body)
        }
      }, 200)
    })
  }
  return instance({ url, method, data, params })
}