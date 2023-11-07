import styles from "./DetailFeed.module.css";
import { useEffect, useState } from "react";
import FileDownloadOutlinedIcon from "@mui/icons-material/FileDownloadOutlined";
import DeleteOutlinedIcon from "@mui/icons-material/DeleteOutlined";
import LoopOutlinedIcon from "@mui/icons-material/LoopOutlined";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useLocation } from "react-router-dom";
function DetailFeed() {
  const location = useLocation();
  const data = location.state;
  const [user, setUser] = useState(null);
  const [state, setState] = useState(null);
  const navigate = useNavigate();
  const BASE_URL = `http://k9e203.p.ssafy.io`;
  const [accessToken, setAccessToken] = useState(
    sessionStorage.getItem("accessToken")
  );
  useEffect(() => {
    axios
      .get(`${BASE_URL}/api/v1/members`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      })
      .then((response) => {
        setUser(response.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);
  const getDetailFeed = () => {
    axios
      .get(`${BASE_URL}/api/v1/feeds/${data.id}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      })
      .then((response) => {
        setState(response.data);
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const setIsPublic = () => {
    const currentState = { ...state };
    setState(currentState);
    if (currentState.isPublic) {
      currentState.isPublic = false;
    } else {
      currentState.isPublic = true;
    }
    setState(currentState);
  };

  const gotodetail = () => {
    navigate(`/detailcomment/${state.id}`, { state });
  };
  const download = () => {
    // 여기서 파일 다운로드 로직 실행
  };

  const deleteFeed = () => {
    // 여기서 피드 삭제 로직 실행
  };
  useEffect(() => {
    getDetailFeed();
    console.log(state);
  }, []);
  return (
    <div>
      {state ? (
        <div className={styles.container}>
          {state && (
            <>
              <div className={styles.lock}>
                {user.id != state.writer.email ? (
                  <></>
                ) : state.isPublic ? (
                  <LockOutlinedIcon
                    onClick={setIsPublic}
                    className={styles.icon}
                  />
                ) : (
                  <LockOpenIcon onClick={setIsPublic} className={styles.icon} />
                )}
              </div>

              <img src={state.thumbnailUrl} className={styles.imageContainer} />
              <div className={styles.etcbox}>
                <div className={styles.etcinner} onClick={gotodetail}>
                  <LoopOutlinedIcon className={styles.etcimg} />
                  댓글보기
                </div>
                {user.id != state.id && (
                  <div className={styles.etcinner}>
                    <LoopOutlinedIcon className={styles.etcimg} />
                    이어받기
                  </div>
                )}
                {user.id == state.writer.email && (
                  <>
                    <div className={styles.etcinner} onClick={download}>
                      <FileDownloadOutlinedIcon className={styles.etcimg} />
                      다운로드
                    </div>
                    <div className={styles.etcinner} onClick={deleteFeed}>
                      <DeleteOutlinedIcon className={styles.etcimg} />
                      삭제
                    </div>
                  </>
                )}
              </div>
            </>
          )}
        </div>
      ) : (
        <></>
      )}
    </div>
  );
}

export default DetailFeed;
