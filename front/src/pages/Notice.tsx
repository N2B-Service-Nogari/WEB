import React from 'react'
import { Helmet } from 'react-helmet-async'

import CampaignOutlinedIcon from '@mui/icons-material/CampaignOutlined'
import styled, { keyframes } from 'styled-components'

function Notice() {
  return (
    <Container>
      <div
        style={{
          color: 'black',
          fontWeight: 'bold',
          fontSize: '24px',
          marginBottom: '30px',
        }}
      >
        <div>
          <CampaignOutlinedIcon
            sx={{
              float: 'left',
              color: 'black',
              width: 50,
              height: 32,
              marginLeft: '-18px',
            }}
          />{' '}
        </div>
        <div style={{ marginLeft: '0px', fontFamily: 'Public Sans' }}>
          Notice
        </div>
      </div>
      <TimelineList>
        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 23일</Date>
            <Title>릴리즈 노트(v.1.0.1)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Chrome Extension의 Chrome WebStore 게시가 다시 시작되었습니다.{' '}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 22일</Date>
            <Title>릴리즈 노트(v.1.0.1)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Tistory 수정요청시, 요청 링크(Request Link) 에러에 대한 버그가
              해결되었습니다. {'\n'}
              2. Tistory 및 Github 발행 이력 테이블의 발행상태에 대한 시각화가
              적용되었습니다. {'\n'}
              3. Tistory 및 Github 발행 이력 테이블 입력 및 수정시 발생했던
              버그가 해결되었습니다. {'\n'}
              4. Chrome Extension 발행 이력 테이블의 발행상태에 대한 시각화가
              적용되었습니다. {'\n'}
              5. Chrome Extension 발행 이력 중, 발행일자에 UTC+9를
              적용하였습니다. {'\n'}
              6. Chrome Extension 일부 변경으로 인해, Chrome WebStore에서 게시가
              일시 중단되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 18일</Date>
            <Title>릴리즈 노트(v.1.0.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Nogari의 오픈 베타 서비스가 시작되었습니다. {'\n'}
              2. 첫 방문시 등장하는 튜토리얼 페이지가 추가되었습니다. {'\n'}
              3. 발행 이력 테이블에 Notion 토큰 검사가 추가되었습니다. {'\n'}
              4. 메인 페이지 일부 디자인이 수정되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 17일</Date>
            <Title>릴리즈 노트(v.0.6.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. 전체 기능을 대상으로, API 테스팅 및 부하 테스팅이
              진행되었습니다. {'\n'}
              2. 발행 이력 테이블의 View Mode, Edit Mode 관련 디버깅이
              진행되었습니다. {'\n'}
              3. Notion, Tistory, Github 토큰의 암호화 및 복호화가
              적용되었습니다. {'\n'}
              4. Chrome Extension 서비스의 CSS 수정 작업이 진행되었습니다.{' '}
              {'\n'}
              5. 메인 페이지 일부 디자인이 수정되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 16일</Date>
            <Title>릴리즈 노트(v.0.5.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Nogari의 Chrome Extension이 정식 배포되었습니다. {'\n'} ➜&nbsp;
              <a
                href="https://chrome.google.com/webstore/detail/nogari-%EB%85%B8%EC%85%98%EC%97%90%EC%84%9C-%EA%B0%80%EB%8A%94-%EC%9D%B4%EC%95%BC%EA%B8%B0/hjdmhaniikfbncdhikfbgfkpchicegfp?hl=ko"
                rel="noreferrer"
                target="_blank"
              >
                Chrome Extension 설치 페이지 바로가기
              </a>
              {'\n'}
              2. Github 발행이력 조회 기능이 구현되었습니다. {'\n'}
              3. Google Form 기반의 설문조사 페이지가 추가되었습니다. {'\n'}
              4. 개인정보 처리방침 페이지가 추가되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 12일</Date>
            <Title>릴리즈 노트(v.0.4.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. 공개여부가 비공개일때, 발행되지 않던 문제가 수정되었습니다.{' '}
              {'\n'}
              2. Notion, Tistory, Github의 토큰 발급 상태를 확인하는 기능이
              추가되었습니다. {'\n'}
              3. Chrome Extension의 설정 페이지 구현이 완료되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 08일</Date>
            <Title>릴리즈 노트(v.0.3.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Tistory 발행 프로세스 진행시, 사용자의 요청 순서와 발행 순서가
              일치하지 않는 문제가 수정되었습니다. {'\n'}
              2. 회원가입시 이메일 중복 검사 기능이 구현되었습니다. {'\n'}
              3. Chrome Extension의 로그인 페이지 구현이 완료되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 05월 03일</Date>
            <Title>릴리즈 노트(v.0.2.1)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Notion To Tistory 발행 및 수정 프로세스에 멀티스레딩이
              적용되었습니다. {'\n'}
              2. Notion, Tistory, Github의 토큰 발급 방식이 변경되었습니다.{' '}
              {'\n'}
              3. Nogari 로딩 창 구현이 완료되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 04월 28일</Date>
            <Title>릴리즈 노트(v.0.2.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Notion 페이지에 업로드된 이미지를 Tistory에 순서대로 발행하는
              기능이 구현되었습니다. {'\n'}
              2. AWS Lambda 함수의 URL 발행이 완료되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 04월 27일</Date>
            <Title>릴리즈 노트(v.0.1.1)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Nogari 서비스가 AWS EC2에 정식 배포되었습니다. {'\n'}
              2. Tistory Access Token 및 Github Access Token 연동 기능이
              구현되었습니다. {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>

        <TimelineItemContainer>
          <TimelineItem>
            <Date>2023년 04월 25일</Date>
            <Title>릴리즈 노트(v.0.1.0)</Title>
            <Contents style={{ whiteSpace: 'pre-wrap' }}>
              1. Notion 페이지를 마크다운(.md)으로 변환하는 파싱 함수가
              구현되었습니다. {'\n'}
              2. AWS Lambda에 JavaScript 기반의 파싱 함수 적재가 완료되었습니다.{' '}
              {'\n'}
            </Contents>
          </TimelineItem>
        </TimelineItemContainer>
      </TimelineList>
    </Container>
  )
}

const Container = styled.div`
  color: white;
  max-width: 100%;
  margin: 0px auto 0;
  padding: 0px;
  background: #f9fafb;
  box-sizing: border-box;
`

const TimelineList = styled.ul`
  margin: 0;
  padding: 0;
  border-left: 2px solid rgba(255, 202, 18, 0.4);
  list-style: none;
`

const TimelineItemContainer = styled.li`
  list-style: none;
`

const Date = styled.span`
  padding: 4px 12px;
  background: #ffca12;
  color: black;
  border-radius: 16px;
  font-weight: bold;
`

const Title = styled.h3`
  margin: 16px 0 0;
  padding: 0;
  color: rgba(0, 0, 0, 0.7);
  opacity: 1;
`

const Contents = styled.p`
  margin: 8px 0 0;
  color: rgba(0, 0, 0, 0.5);
`

const animate = keyframes`
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
`

const TimelineItem = styled.div`
  position: relative;
  padding: 28px 20px;
  &:hover {
    background: rgba(105, 105, 105, 0.2);
    ${Date} {
      background: black;
      color: #ffca12;
      font-weight: bold;
    }
    ${Title} {
      color: black;
      font-weight: bold;
    }
    ${Contents} {
      color: black;
    }
  }
  &::before {
    content: '';
    position: absolute;
    top: 32px;
    left: -7px;
    width: 12px;
    height: 12px;
    background: rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    box-shadow: inset 0 0 10px rgba(255, 202, 18, 1);
  }
  &::after {
    content: '';
    position: absolute;
    top: 32px;
    left: -7px;
    width: 12px;
    height: 12px;
    background: white;
    border-radius: 50%;
    box-shadow: inset 0 0 10px #ffca12;
    opacity: 0;
  }
  &:hover::after {
    animation: ${animate} 0.5s linear infinite;
  }
`

export default Notice
