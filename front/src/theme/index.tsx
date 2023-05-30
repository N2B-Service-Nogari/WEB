import { useMemo } from 'react'

// @mui
import { CssBaseline, ThemeOptions } from '@mui/material'
import {
  ThemeProvider as MUIThemeProvider,
  createTheme,
  StyledEngineProvider,
} from '@mui/material/styles'
import PropTypes from 'prop-types'

//
import customShadows from './customShadows'
import GlobalStyles from './globalStyles'
import componentsOverride from './overrides'
import palette from './palette'
import shadows from './shadows'
import typography from './typography'

// ----------------------------------------------------------------------
ThemeProvider.propTypes = {
  children: PropTypes.node,
}

export default function ThemeProvider({ children }: any) {
  const themeOptions = useMemo<ThemeOptions>(
    () => ({
      palette,
      shape: { borderRadius: 6 },
      typography,
      shadows: shadows(),
      customShadows: customShadows(),
    }),
    []
  )

  const theme = createTheme(themeOptions)
  theme.components = componentsOverride(theme)

  return (
    <StyledEngineProvider injectFirst>
      <MUIThemeProvider theme={theme}>
        <CssBaseline />
        <GlobalStyles />
        {children}
      </MUIThemeProvider>
    </StyledEngineProvider>
  )
}
