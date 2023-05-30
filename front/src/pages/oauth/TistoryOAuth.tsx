import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

import { postOauthTistory } from '@/apis/OauthApis'
import LoadingSpinner from '@/components/loading'
import { loadingInterceptors } from '@/components/loading/LoadingInterceptors'

function TistoryOAuth() {
  const [loading, setLoading] = useState(false)
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  useEffect(() => {
    loadingInterceptors(setLoading)
    const code = searchParams.get('code')
    ;(async function () {
      if (code) {
        try {
          const response = await postOauthTistory(code)

          const resultCode = response.data.resultCode
          if (resultCode === 200) {
            alert('티스토리 연동이 완료되었습니다.')
            navigate('/tistory')
          }
        } catch (error) {
          console.log(error)
        }
      }
    })()
  }, [searchParams])
  return <>{loading && <LoadingSpinner />}</>
}

export default TistoryOAuth
