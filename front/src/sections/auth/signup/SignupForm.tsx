import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'

// @mui
import { LoadingButton } from '@mui/lab'
import { Stack, IconButton, InputAdornment } from '@mui/material'

import { getCheckEmail, postRegister } from '@/apis/authApis'
import Iconify from '@/components/iconify'

// react-hook-form
import InputText from '@/components/input-text/InputText'

interface ILoginValue {
  email: string
  password: string
  passwordConfirm: string
}

// 이메일 & 비밀번호 유효성 검사 형식
const Regex = {
  email: /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/g,
  password: /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[~?!@#$%^&*_-]).{8,}$/,
}

function SignupForm() {
  const navigate = useNavigate()
  // 이메일 중복확인 검사
  const [emailDuplicate, setEmailDuplicate] = useState(true)

  // form 생성
  const {
    watch,
    control,
    handleSubmit,
    formState: { isValid },
    getFieldState,
    clearErrors,
  } = useForm<ILoginValue>({
    mode: 'all',
  })
  const [showPassword, setShowPassword] = useState(false)
  const [showPasswordConfirm, setShowPasswordConfirm] = useState(false)

  // replace : true 를 적용해서 뒤로가기가 안되게 적용하였습니다.

  // 이메일 중복확인 fucntion
  const emailHandler = async () => {
    const email = watch('email')
    const emailState = getFieldState('email')

    // 이메일이 없거나 이메일 형식에 안맞는 경우 return
    if (email && emailState.error?.type == 'emailvalidate') {
      try {
        const response = await getCheckEmail(email)
        // console.log(response)
        if (response.data.resultCode === 200) {
          setEmailDuplicate(response.data.result)
          alert(response.data.resultMessage)
          if (!response.data.result) {
            clearErrors('email')
          }
        }
      } catch (error: any) {
        console.log(error)
      }
    }
  }
  // form 제출 handler
  const submitHandler = async (data: ILoginValue) => {
    // console.log(data)
    try {
      const response = await postRegister(data)
      if (response.data.resultCode === 200) {
        alert(response.data.resultMessage)
        navigate('/')
      }
    } catch (error: any) {
      console.log(error)
    }
    navigate('/', { replace: true })
  }

  // 비밀번호 확인
  const checkpassword = (val: string) => {
    if (watch('password') != val) {
      return '비밀번호가 일치하지 않습니다.'
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
            rules={{
              required: '이메일을 입력해주세요',
              pattern: {
                value: Regex.email,
                message: '이메일 형식을 입력해주세요',
              },
              validate: {
                emailvalidate: () =>
                  !emailDuplicate || '이메일 중복확인을 해주세요',
              },
              onChange: () => {
                setEmailDuplicate(true)
              },
            }}
            textFieldProps={{
              label: 'Email',
              InputProps: {
                endAdornment: (
                  <InputAdornment position="end">
                    <LoadingButton onClick={emailHandler}>
                      중복검사
                    </LoadingButton>
                  </InputAdornment>
                ),
              },
            }}
          />

          <InputText
            control={control}
            defaultValue=""
            name="password"
            rules={{
              required: '비밀번호를 입력해주세요',
              pattern: {
                value: Regex.password,
                message: '대문자, 숫자, 특수문자를 포함해 8자 이상입력해주세요',
              },
            }}
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
          <InputText
            control={control}
            defaultValue=""
            name="passwordConfirm"
            rules={{
              required: '비밀번호를 입력해주세요',
              validate: { check: checkpassword },
            }}
            textFieldProps={{
              label: 'Password Confirm',
              type: `${showPasswordConfirm ? 'text' : 'password'}`,
              InputProps: {
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      edge="end"
                      onClick={() =>
                        setShowPasswordConfirm(!showPasswordConfirm)
                      }
                    >
                      <Iconify
                        icon={
                          showPasswordConfirm
                            ? 'eva:eye-fill'
                            : 'eva:eye-off-fill'
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
            Sign up
          </LoadingButton>
        </Stack>
      </form>
    </>
  )
}

export default SignupForm
