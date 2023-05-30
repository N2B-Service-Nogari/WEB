import { Outlet } from 'react-router-dom'

// @mui
import { styled } from '@mui/material/styles'

// components
import { ReactComponent as Logo } from '@/assets/logos/nogari_logo.svg'

// ----------------------------------------------------------------------

const StyledHeader = styled('header')(({ theme }) => ({
  top: 0,
  left: 0,
  lineHeight: 0,
  width: '100%',
  position: 'absolute',
  padding: theme.spacing(3, 3, 0),
  [theme.breakpoints.up('sm')]: {
    padding: theme.spacing(5, 5, 0),
  },
}))

const StyleLogoBox = styled('div')(({ theme }) => ({
  width: '200px',
  // height: '40px',
}))

// ----------------------------------------------------------------------

function RegisterLayout() {
  return (
    <>
      <StyledHeader>
        <StyleLogoBox>
          <Logo />
        </StyleLogoBox>
      </StyledHeader>
      <Outlet />
    </>
  )
}

export default RegisterLayout
