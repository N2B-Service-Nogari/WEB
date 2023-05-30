import { createBrowserRouter } from 'react-router-dom'

import RegisterLayout from './layouts/register/RegisterLayout'
import RootLayout from './layouts/root/RootLayout'
import GithubPage from './pages/GithubPage'
import LoginPage from './pages/LoginPage'
import Notice from './pages/Notice'
import GithubOAuth from './pages/oauth/GithubOAuth'
import NotionOAuth from './pages/oauth/NotionOAuth'
import TistoryOAuth from './pages/oauth/TistoryOAuth'
import Page404 from './pages/Page404'
import Privacy from './pages/Privacy'
import SignupPage from './pages/SignupPage'
import TistoryPage from './pages/TistoryPage'
import PrivateRouter from './routers/PrivateRouter'

const routers = createBrowserRouter([
  // Tistory, Gihub 페이지는 로그인 후에만 이동할 수 있도록 처리

  // Oauth 페이지 또한 로그인 후에만 이동할 수 있도록 처리
  {
    element: <PrivateRouter authentication={true} />,
    errorElement: <Page404 />,
    children: [
      {
        element: <RootLayout />,
        children: [
          {
            path: '/github',
            element: <GithubPage />,
          },
          {
            path: '/tistory',
            element: <TistoryPage />,
          },

          {
            path: '/notice',
            element: <Notice />,
          },
        ],
      },
      {
        path: '/oauth/notion',
        element: <NotionOAuth />,
      },
      {
        path: '/oauth/tistory',
        element: <TistoryOAuth />,
      },
      {
        path: '/oauth/github',
        element: <GithubOAuth />,
      },
    ],
  },

  // 회원가입, 로그인은 로그인 하지 않은 유저만 접근가능하도록 처리
  {
    element: <PrivateRouter authentication={false} />,
    errorElement: <Page404 />,
    children: [
      {
        element: <RegisterLayout />,
        children: [
          {
            path: '/',
            element: <LoginPage />,
          },
          {
            path: '/signup',
            element: <SignupPage />,
          },
        ],
      },
    ],
  },

  // 모든 사용자가 접근 가능
  {
    errorElement: <Page404 />,
    children: [
      {
        element: <Privacy />,
        path: '/privacy',
      },
    ],
  },
])

export default routers
