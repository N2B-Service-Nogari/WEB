import React, { useState, useEffect } from 'react'
import { Outlet } from 'react-router-dom'

import HelpOutlineIcon from '@mui/icons-material/HelpOutline'
import { Stack, IconButton } from '@mui/material'
import { styled } from '@mui/material/styles'

import SideBar from './sideBar/SideBar'

import { postLogOut } from '@/apis/authApis'
import Iconify from '@/components/iconify'
import LoadingSpinner from '@/components/loading'
import { loadingInterceptors } from '@/components/loading/LoadingInterceptors'
import Tutorial from '@/components/tutorial/Tutorial'

function RootLayout() {
  const [loading, setLoading] = useState(false)
  const [tutorial, setTutorial] = useState(false)

  useEffect(() => {
    loadingInterceptors(setLoading)
    if (!localStorage.getItem('tutorial')) setTutorial(true)
  }, [])

  const logoutHandler = async () => {
    const response = await postLogOut()
    if (response.data.resultCode === 200) {
      sessionStorage.clear()
      window.location.replace('/')
    }
    // console.log(response)
  }

  return (
    <>
      {/* 좌측 패딩 */}
      <StyledLeftBodyPadding>
        {/* 로그아웃 */}
        <StyledIconPadding>
          <Stack direction="row" justifyContent="end" width="100%">
            <IconButton title="로그아웃" onClick={logoutHandler}>
              <Iconify icon={'ic:baseline-logout'} />
            </IconButton>
          </Stack>
        </StyledIconPadding>
        <SideBar />

        {/* 아울렛, 전체적인 패딩 */}
        <StyledContainer>
          <Outlet />
        </StyledContainer>
      </StyledLeftBodyPadding>
      {/* 로딩스피너 */}
      {loading && <LoadingSpinner />}
      {/* 튜토리얼 */}
      {tutorial && <Tutorial setTutorial={setTutorial} />}
      <div
        title="사이트 이용법"
        style={{
          position: 'fixed',
          right: '70px',
          bottom: '70px',
          zIndex: 1000,
          cursor: 'pointer',
        }}
        onClick={() => setTutorial(true)}
      >
        <HelpOutlineIcon
          sx={{
            width: '40px',
            height: '40px',
            backgroundColor: 'white',
            borderRadius: '100px',
          }}
        />
      </div>
    </>
  )
}

const StyledIconPadding = styled('div')({
  paddingTop: '30px',
  paddingLeft: '30px',
  paddingRight: '30px',
})

const StyledLeftBodyPadding = styled('div')({
  paddingLeft: '280px',
})

const StyledContainer = styled('div')(({ theme }) => ({
  marginLeft: '40px',
  marginRight: '40px',
}))

export default RootLayout
