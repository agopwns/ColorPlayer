# ColorPlayer
안드로이드 기반 음악 플레이어 어플입니다.  
  
음악 플레이어 오픈소스로 유명한 [Timber](https://github.com/naman14/Timber)를 많이 참고하여 <b>GNU 라이센스</b>를 따릅니다.  
  
나만의 음악 어플을 만들어보고 싶었고  
  
해당 작품을 통해 안드로이드의 Service와 Media Player를 많이 활용해보고 싶어서 개발하였습니다.   
   
작품의 상세 작동 영상은 [여기](https://youtu.be/xRRacTuoMQE)에서 보실 수 있습니다.  

## 스크린샷
<div style="float:left;">
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/1playList.jpg" alt="Your image title" width="49%"/> 
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/2player.jpg" alt="Your image title" width="49%"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/3album.jpg" alt="Your image title" width="49%"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/4customList.jpg" alt="Your image title" width="49%"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/5yotubePlay.jpg" alt="Your image title" width="49%"/>
  <img src="https://github.com/agopwns/ColorPlayer/blob/master/images/6eventPage.jpg" alt="Your image title" width="49%"/>
</div>

## 기능
  - 기본 음악 플레이어 기능(재생, 일시정지, 앞으로, 뒤로, 재생바, 랜덤 재생, 반복)
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
License
(c) 2020 agopwns  
  
This is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.  
  
This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.  
  
You should have received a copy of the GNU General Public License along with this app. If not, see https://www.gnu.org/licenses/.
  
