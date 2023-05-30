import { whiteList } from './LoadingWhitelist'

import { axAuth, axBase } from '@/apis/axiosInstance'

export const loadingInterceptors = (
  setLoading: React.Dispatch<React.SetStateAction<boolean>>
) => {
  //axios 호출시 인터셉트
  axBase.interceptors.request.use(
    (config) => {
      if (whiteList.includes(config.url)) setLoading(true)
      return config
    },
    (error) => {
      setLoading(false)
      return Promise.reject(error)
    }
  )
  axAuth.interceptors.request.use(
    (config) => {
      if (whiteList.includes(config.url)) setLoading(true)
      return config
    },
    (error) => {
      setLoading(false)
      return Promise.reject(error)
    }
  )

  //axios 호출 종료시 인터셉트
  axBase.interceptors.response.use(
    (response) => {
      setLoading(false)
      return response
    },
    (error) => {
      setLoading(false)
      return Promise.reject(error)
    }
  )
  axAuth.interceptors.response.use(
    (response) => {
      setLoading(false)
      return response
    },
    (error) => {
      setLoading(false)
      return Promise.reject(error)
    }
  )
}
