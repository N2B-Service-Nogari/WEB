import React from 'react'
import { Helmet } from 'react-helmet-async'

// @mui
import {
  Link,
  Container,
  Typography,
  Divider,
  Stack,
  Button,
} from '@mui/material'
import { styled } from '@mui/material/styles'

// components
import Iconify from '@/components/iconify/Iconify'
import { SignupForm } from '@/sections/auth/signup'

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
  // boxShadow: theme.customShadows.card,
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

function SignupPage() {
  return (
    <>
      <Helmet>
        <title> SignUp | Nogari </title>
      </Helmet>

      <StyledRoot>
        <StyledSection>
          <Typography sx={{ px: 5, mt: 10, mb: 5 }} variant="h3">
            반갑습니다. 들어오십쇼
          </Typography>
          <img alt="login" src="/assets/illustrations/illustration_login.png" />
        </StyledSection>
        <Container maxWidth="sm">
          <StyledContent>
            <Typography gutterBottom sx={{ mb: 5 }} variant="h4">
              노가리 회원가입하기
            </Typography>

            <SignupForm />
          </StyledContent>
        </Container>
      </StyledRoot>
    </>
  )
}

export default SignupPage
