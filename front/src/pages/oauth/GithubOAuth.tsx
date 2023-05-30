import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'

import { postOauthGit } from '@/apis/OauthApis'
import { loadingInterceptors } from '@/components/loading/LoadingInterceptors'
import LoadingSpinner from '@/components/loading/LoadingSpinner'

function GithubOAuth() {
  const [loading, setLoading] = useState(false)
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  useEffect(() => {
    loadingInterceptors(setLoading)
    const code = searchParams.get('code')
    ;(async function () {
      if (code) {
        try {
          const response = await postOauthGit(code)
          const resultCode = response.data.resultCode
          if (resultCode === 200) {
            alert('깃허브 연동이 완료되었습니다.')
            navigate('/github')
          }
        } catch (error) {
          console.log(error)
        }
      }
    })()
  }, [])
  return <>{loading && <LoadingSpinner />}</>
}

export default GithubOAuth
