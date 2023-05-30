import React from 'react'
import { Helmet } from 'react-helmet-async'

// @mui
import { useNavigate } from 'react-router-dom'

import { Link, Container, Typography, Divider } from '@mui/material'
import { styled } from '@mui/material/styles'

// components

import Iconify from '@/components/iconify/Iconify'
import { LoginForm } from '@/sections/auth/login'

// ----------------------------------------------------------------------

const StyledRoot = styled('div')(({ theme }) => ({
  [theme.breakpoints.up('md')]: {
    display: 'flex',
  },
}))

const StyledSection = styled('div')(({ theme }) => ({
  width: '100%',
  maxWidth: 480,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  boxShadow: theme.customShadows?.card,
  backgroundColor: theme.palette.background.default,
}))

const StyledContent = styled('div')(({ theme }) => ({
  maxWidth: 480,
  margin: 'auto',
  minHeight: '100vh',
  display: 'flex',
  justifyContent: 'center',
  flexDirection: 'column',
  padding: theme.spacing(12, 0),
}))

// ----------------------------------------------------------------------
// API 영역

function LoginPage() {
  const navigate = useNavigate()
  return (
    <>
      <Helmet>
        <title> Login | Nogari </title>
      </Helmet>

      <StyledRoot>
        <StyledSection>
          <Typography sx={{ px: 5, mt: 10, mb: 5 }} variant="h3">
            다시왔군요. 가봅시다!
          </Typography>
          <img alt="login" src="/assets/illustrations/illustration_login.png" />
        </StyledSection>
        <Container maxWidth="sm">
          <StyledContent>
            <Typography gutterBottom variant="h4">
              노가리에 로그인 해주세요
            </Typography>

            <Typography sx={{ mb: 5 }} variant="body2">
              아직 계정이 없으신가요? {''}
              <Link
                style={{ cursor: 'pointer' }}
                variant="subtitle2"
                onClick={() => navigate('/signup')}
              >
                회원가입 하기
              </Link>
            </Typography>

            <Divider sx={{ my: 3 }}>
              <Typography sx={{ color: 'text.secondary' }} variant="body2">
                OR
              </Typography>
            </Divider>

            <LoginForm />
          </StyledContent>
        </Container>
      </StyledRoot>
    </>
  )
}

export default LoginPage
