import { useState } from 'react'

import { loadingTips } from './LoadingTips'

import { ReactComponent as Loading_spinner } from '@/assets/logos/nogari_spinner.svg'
import style from '@/styles/components/loading/LoadingSpinner.module.css'

function LoadingSpinner() {
  const randomNum = Math.floor(Math.random() * loadingTips.length)
  const [tip, setTip] = useState(loadingTips[randomNum])

  setTimeout(() => {
    const randomNum = Math.floor(Math.random() * loadingTips.length)
    setTip(loadingTips[randomNum])
  }, 7000)

  return (
    <div className={style.Wrapper}>
      <div className={style.SvgWrapper}>
        <Loading_spinner className={style.BingGleBingGle} />
        <p className={style.Text}>잠시만 기다려주세요...</p>
      </div>
      <p className={style.Tips}>{tip}</p>
    </div>
  )
}

export default LoadingSpinner
