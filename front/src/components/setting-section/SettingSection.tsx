import { Link } from 'react-router-dom'

// @mui
import { Box, List, ListItemText, ListItemButton } from '@mui/material'

import { StyledSettingItem, StyledSettingItemIcon } from './styles'

// ----------------------------------------------------------------------
interface SettingConfig {
  title: string
  path: string
  icon?: JSX.Element
}

export default function Section({
  data = [],
  ...other
}: {
  data: SettingConfig[]
}) {
  return (
    <Box {...other}>
      <List disablePadding sx={{ p: 1 }}>
        {data.map((item) => (
          <SettingItem key={item.title} item={item} />
        ))}
      </List>
    </Box>
  )
}

// ----------------------------------------------------------------------

function SettingItem({ item }: { item: SettingConfig }) {
  const { title, path, icon } = item

  return (
    <StyledSettingItem
      component={Link}
      target={path === '/notice' ? '_self' : '_blank'}
      to={path}
    >
      <StyledSettingItemIcon>{icon && icon}</StyledSettingItemIcon>
      <ListItemText disableTypography primary={title} />
    </StyledSettingItem>
  )
}
