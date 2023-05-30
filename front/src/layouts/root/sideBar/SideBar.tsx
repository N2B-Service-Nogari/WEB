// @mui
import { useEffect, useState } from 'react'

import { useQuery } from 'react-query'

import { Link } from 'react-router-dom'

import { Box, Drawer, Typography, Avatar } from '@mui/material'
import { styled, alpha } from '@mui/material/styles'

// mock
import { navConfig, settingConfig, connectedConfig } from './config'

import { StyledNavContent, StyledNavConnectedSite } from './styles'

// hooks
import { getOauthStatus } from '@/apis/OauthApis'
import { ReactComponent as Logo } from '@/assets/logos/nogari_logo.svg'
import ConnectedSection from '@/components/connected-section'
import NavSection from '@/components/nav-section'
import SettingSection from '@/components/setting-section'

// components

// ----------------------------------------------------------------------

const NAV_WIDTH = 280

const StyledAccount = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  padding: theme.spacing(2, 2.5),
  borderRadius: Number(theme.shape.borderRadius) * 1.5,
  backgroundColor: alpha(theme.palette.grey[500], 0.12),
}))

// ----------------------------------------------------------------------

export default function Nav() {
  const [avatarNum, setAvatarNum] = useState(1)
  useEffect(() => {
    setAvatarNum(Math.floor(Math.random() * 25 + 1))
  }, [])

  const { data } = useQuery('oauths', getOauthStatus)
  const renderContent = (
    <>
      <Box sx={{ px: 2.5, py: 3, display: 'inline-flex', paddingLeft: '25px' }}>
        <Link to="/notice">
          <Logo width="150px" />
        </Link>
      </Box>

      <Box sx={{ mb: 5, mx: 2.5 }}>
        <StyledAccount>
          <Avatar
            alt="photoURL"
            src={`/assets/images/avatars/avatar_${avatarNum}.jpg`}
          />

          <Box
            sx={{
              ml: 2,
              textOverflow: 'ellipsis',
              overflow: 'hidden',
              whiteSpace: 'nowrap',
            }}
          >
            <Typography
              variant="subtitle2"
              sx={{
                color: 'text.primary',
              }}
            >
              {sessionStorage.getItem('email')?.split('@')[0]}
            </Typography>

            <Typography sx={{ color: 'text.secondary' }} variant="body2">
              {'일반회원'}
            </Typography>
          </Box>
        </StyledAccount>
      </Box>
      <StyledNavContent>
        <NavSection data={navConfig} />
        <div>
          <div style={{ padding: '0 22px' }}>
            <p style={{ fontSize: '17px', margin: '5px 0' }}>사이트 연동하기</p>
            <StyledNavConnectedSite>
              <ConnectedSection
                data={connectedConfig(
                  data?.data.result.notion,
                  data?.data.result.tistory,
                  data?.data.result.github
                )}
              />
            </StyledNavConnectedSite>
          </div>
          <div
            style={{
              borderBottom: 'solid',
              borderBottomWidth: '1px',
              borderBottomColor: '#E0E4E8',
              margin: '15px 22px 0 22px',
            }}
          ></div>
          <SettingSection data={settingConfig} />
        </div>
      </StyledNavContent>
    </>
  )

  return (
    <Box
      component="nav"
      sx={{
        flexShrink: { lg: 0 },
        width: { lg: NAV_WIDTH },
      }}
    >
      <Drawer
        open
        variant="permanent"
        PaperProps={{
          sx: {
            width: NAV_WIDTH,
            bgcolor: 'background.default',
            borderRightStyle: 'dashed',
          },
        }}
      >
        {renderContent}
      </Drawer>
    </Box>
  )
}
