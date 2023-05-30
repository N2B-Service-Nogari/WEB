import React, { useState } from 'react'

import tutorial1 from '@/assets/tutorial/step1.png'
import tutorial2 from '@/assets/tutorial/step2.png'
import tutorial3 from '@/assets/tutorial/step3.png'
import tutorial4 from '@/assets/tutorial/step4.png'
import style from '@/styles/components/tutorial/tutorial.module.css'

function Tutorial({
  setTutorial,
}: {
  setTutorial: React.Dispatch<React.SetStateAction<boolean>>
}) {
  const [imgsrc, setImgSrc] = useState({ id: 1, img: tutorial1 })
  const handleImg = () => {
    if (imgsrc.id === 4) {
      setTutorial(false)
      localStorage.setItem('tutorial', 'true')
    } else if (imgsrc.id === 1) {
      setImgSrc({ id: 2, img: tutorial2 })
    } else if (imgsrc.id === 2) {
      setImgSrc({ id: 3, img: tutorial3 })
    } else if (imgsrc.id === 3) {
      setImgSrc({ id: 4, img: tutorial4 })
    }
  }
  return (
    <div className={style.Wrapper}>
      <img
        alt="튜토리얼"
        src={imgsrc.img}
        style={{ width: '100%', height: '100%' }}
        onClick={handleImg}
      />
    </div>
  )
}

export default Tutorial
