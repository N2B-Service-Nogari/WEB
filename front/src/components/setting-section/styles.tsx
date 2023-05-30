// @mui
import { ListItemIcon, ListItemButton } from '@mui/material'
import { styled } from '@mui/material/styles'

// ----------------------------------------------------------------------

export const StyledSettingItem = styled((props: any) => (
  <ListItemButton disableGutters {...props} />
))(({ theme }) => ({
  ...theme.typography.body2,
  height: 42,
  position: 'relative',
  textTransform: 'capitalize',
  color: theme.palette.text.secondary,
  borderRadius: theme.shape.borderRadius,
}))

export const StyledSettingItemIcon = styled(ListItemIcon)({
  color: 'inherit',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
})
