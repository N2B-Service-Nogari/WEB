import { axAuth } from './axiosInstance'

// 블로그명과 카테고리 조회를 위한 api
export async function postGithubCategory() {
  const response = await axAuth.post(`/contents/github`, {
    lastGithubId: -1,
    pageSize: 1,
  })
  return response
}

// 전체 포스트 리스트 조회를 위한 api

export async function postGithubPostList() {
  const response = await axAuth.post(`/contents/github`, {
    lastGithubId: -1,
    pageSize: 100,
    filter: '최신순',
  })
  return response
}

// github 발행 api
export async function postGithubPost(data: any) {
  const response = await axAuth.post('/contents/git/post', data)
  return response
}
