import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'

// @mui
import { LoadingButton } from '@mui/lab'
import { Link, Stack, IconButton, InputAdornment } from '@mui/material'

// api
import axios from 'axios'

import { postEmailLogin } from '@/apis/authApis'
import Iconify from '@/components/iconify'

// react-hook-form
import InputText from '@/components/input-text/InputText'

interface ILoginInput {
  email: string
  password: string
}

function LoginForm() {
  const navigate = useNavigate()

  // form 생성
  const {
    control,
    handleSubmit,
    formState: { isValid },
  } = useForm<ILoginInput>({})
  const [showPassword, setShowPassword] = useState(false)

  // replace : true 를 적용해서 뒤로가기가 안되게 적용하였습니다.
  // form 제출 handler
  const submitHandler = async (data: ILoginInput) => {
    // console.log(data);
    try {
      const response = await postEmailLogin(data)
      // response 요청 성공시
      if (response.data.resultCode === 200) {
        sessionStorage.setItem(
          'accessToken',
          response.data.result.token.access_token
        )
        sessionStorage.setItem(
          'refreshToken',
          response.data.result.token.refresh_token
        )
        sessionStorage.setItem('email', response.data.result.email)
        navigate('/tistory', { replace: true })
      }
    } catch (error: any) {
      console.log(error)
      alert(error.response.data)
    }
  }

  return (
    <>
      <form onSubmit={handleSubmit(submitHandler)}>
        <Stack spacing={3}>
          <InputText
            control={control}
            defaultValue=""
            name="email"
            rules={{ required: '이메일을 입력해주세요' }}
            textFieldProps={{
              label: 'Email',
            }}
          />
          <InputText
            control={control}
            defaultValue=""
            name="password"
            rules={{ required: '비밀번호를 입력해주세요' }}
            textFieldProps={{
              label: 'Password',
              type: `${showPassword ? 'text' : 'password'}`,
              InputProps: {
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      edge="end"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <Iconify
                        icon={
                          showPassword ? 'eva:eye-fill' : 'eva:eye-off-fill'
                        }
                      />
                    </IconButton>
                  </InputAdornment>
                ),
              },
            }}
          />

          <LoadingButton
            fullWidth
            disabled={isValid ? false : true}
            size="large"
            type="submit"
            variant="contained"
          >
            Login
          </LoadingButton>
        </Stack>
      </form>
    </>
  )
}

export default LoginForm
