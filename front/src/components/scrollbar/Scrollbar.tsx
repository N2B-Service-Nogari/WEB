import { memo } from 'react'

// @mui
import { Box, SxProps, Theme } from '@mui/material'
import PropTypes from 'prop-types'

//
import { StyledRootScrollbar, StyledScrollbar } from './styles'

// ----------------------------------------------------------------------

Scrollbar.propTypes = {
  sx: PropTypes.object,
  children: PropTypes.node,
}

function Scrollbar({ children, sx, ...other }: { children: any; sx: any }) {
  const userAgent =
    typeof navigator === 'undefined' ? 'SSR' : navigator.userAgent

  const isMobile =
    /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
      userAgent
    )

  if (isMobile) {
    return (
      <Box sx={{ overflowX: 'auto', ...sx }} {...other}>
        {children}
      </Box>
    )
  }

  return (
    <StyledRootScrollbar>
      <StyledScrollbar clickOnTrack={false} sx={sx} {...other}>
        {children}
      </StyledScrollbar>
    </StyledRootScrollbar>
  )
}

export default memo(Scrollbar)
