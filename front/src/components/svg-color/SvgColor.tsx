import { forwardRef } from 'react'

// @mui
import { Box } from '@mui/material'
import PropTypes from 'prop-types'

// ----------------------------------------------------------------------

const SvgColor = forwardRef(
  ({ src, sx, ...other }: { src: any; sx: any }, ref) => (
    <Box
      ref={ref}
      className="svg-color"
      component="span"
      sx={{
        width: 24,
        height: 24,
        display: 'inline-block',
        bgcolor: 'currentColor',
        mask: `url(${src}) no-repeat center / contain`,
        WebkitMask: `url(${src}) no-repeat center / contain`,
        ...sx,
      }}
      {...other}
    />
  )
)
SvgColor.displayName = 'SvgColor'

export default SvgColor
