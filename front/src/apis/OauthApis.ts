import { axAuth } from './axiosInstance'

// tistory oauth 연결
export async function postOauthTistory(code: string) {
  const response = await axAuth.post(`/oauth/tistory`, {
    code,
  })
  return response
}

// notion oauth 연결
export async function postOauthNotion(code: string) {
  const response = await axAuth.post(`/oauth/notion`, {
    code,
  })
  return response
}

// github oauth 연결
export async function postOauthGit(code: string) {
  const response = await axAuth.post('/oauth/git', {
    code,
  })
  return response
}

// 각 oauth 연결 여부 확인
export async function getOauthStatus() {
  const response = await axAuth.get('/oauth/check')
  return response
}
