# ColorPlayer
기본적인 음악 플레이어입니다.   
  
나만의 음악 어플을 만들어보고 싶었고 해당 작품을 통해 안드로이드의 Service와 Media Player를 많이 활용해보고 싶어서 개발하였습니다.   
  
작품의 상세 작동 영상은 [여기]()에서 보실 수 있습니다.  
<div style="float:left;">
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/1playList.jpg" alt="Your image title" width="440"/> 
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/2player.jpg" alt="Your image title" width="440"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/3album.jpg" alt="Your image title" width="440"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/4customList.jpg" alt="Your image title" width="440"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/5yotubePlay.jpg" alt="Your image title" width="440"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/6eventPage.jpg" alt="Your image title" width="440"/>
</div>

## 기능
  - 기본 음악 플레이어 기능
  - 위젯, 노티피케이션
  - 잠금 화면
  - 노래, 앨범, 가수, 폴더 리스트
  - 커스텀 재생 목록
  - 회원가입, 로그인 (영상에는 넣지 않았습니다)
  - 좋아요, 댓글 기능 (AWS Lambda + RDS)
  - 현재 재생중인 목록
  - 유튜브 검색 재생 (Youtube api)
  - 이벤트 이미지 및 url 등록 (AWS Lambda + RDS + S3)
  - 재생수, 좋아요 기반 추천

## 사용 기술
  - AWS Lambda
  - AWS Gateway
  - AWS RDS(Maria DB)
  - AWS S3
  - Android
  - HTTP - Retrofit2
  - Picasso
  - Room DB
  - Youtube API

## 라이센스
The MIT License (MIT)  
  
Copyright (c) 2020 agopwns
    
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:  
  
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.  
  
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
