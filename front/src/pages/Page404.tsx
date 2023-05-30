import React from 'react'

import { Helmet } from 'react-helmet-async'
import { Link as RouterLink } from 'react-router-dom'

// @mui
import { Button, Typography, Container, Box } from '@mui/material'
import { styled } from '@mui/material/styles'

// ----------------------------------------------------------------------

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

function Page404() {
  return (
    <>
      <Helmet>
        <title> 404 Page Not Found | Nogari </title>
      </Helmet>

      <Container>
        <StyledContent sx={{ textAlign: 'center', alignItems: 'center' }}>
          <Typography paragraph variant="h3">
            Sorry, page not found!
          </Typography>

          <Typography sx={{ color: 'text.secondary' }}>
            죄송합니다. 페이지를 찾을 수 없습니다. URL을 한번 더 확인해주세요.
          </Typography>

          <Box
            component="img"
            src="/assets/illustrations/illustration_404.svg"
            sx={{ height: 260, mx: 'auto', my: { xs: 5, sm: 10 } }}
          />

          <Button
            component={RouterLink}
            size="large"
            to="/test"
            variant="contained"
          >
            Home
          </Button>
        </StyledContent>
      </Container>
    </>
  )
}

export default Page404
