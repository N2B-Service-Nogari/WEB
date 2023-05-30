import {
  useController,
  FieldValues,
  FieldPath,
  UseControllerProps,
} from 'react-hook-form'

import { TextField, TextFieldProps } from '@mui/material'

interface MuiProps {
  textFieldProps?: TextFieldProps
}

function InputText<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
>({
  textFieldProps, // textField를 위한 prop들, mui에서 import 해온다.
  ...props
}: MuiProps & UseControllerProps<TFieldValues, TName>) {
  const {
    field,
    fieldState: { error },
  } = useController(props)

  return (
    <TextField
      {...textFieldProps}
      {...field}
      error={!!error}
      helperText={!!error && error.message}
    />
  )
}

export default InputText
