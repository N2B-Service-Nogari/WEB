import { forwardRef } from 'react'

// icons
import { Icon, IconifyIcon } from '@iconify/react'
// @mui
import { Box } from '@mui/material'

// ----------------------------------------------------------------------
interface IconProps {
  icon: string | IconifyIcon
  width?: number | string
  sx?: object
}

// ----------------------------------------------------------------------

const Iconify = forwardRef(
  ({ icon, width = 20, sx, ...other }: IconProps, ref) => (
    <Box
      ref={ref}
      component={Icon}
      icon={icon}
      sx={{ width, height: width, ...sx }}
      {...other}
    />
  )
)

Iconify.displayName = 'Iconify'

export default Iconify
