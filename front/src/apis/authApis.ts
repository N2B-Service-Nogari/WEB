import { axAuth, axBase } from './axiosInstance'

interface ILoginInput {
  email: string
  password: string
}

// 회원가입 이메일 중복확인 api
export async function getCheckEmail(email: string) {
  const response = await axBase.get(`/members/duplicate`, {
    params: {
      email,
    },
  })
  return response
}

// 회원가입 api
export async function postRegister({ email, password }: ILoginInput) {
  const response = await axBase.post(`/members/signup`, {
    email,
    password,
  })
  return response
}

// 로그인(Sign in) api
export async function postEmailLogin({ email, password }: ILoginInput) {
  const response = await axBase.post(`/members/login`, {
    email,
    password,
  })
  return response
}

// 로그아웃 api
export async function postLogOut() {
  const response = await axAuth.post('/members/logout', {
    access_token: sessionStorage.getItem('accessToken'),
    refresh_token: sessionStorage.getItem('refreshToken'),
  })
  return response
}
