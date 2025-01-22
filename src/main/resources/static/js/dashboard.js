document.addEventListener('DOMContentLoaded', function () {
  initializeWebSocket();
});

function initializeWebSocket() {
  const socket = new SockJS('/ws-docker-monitor');
  const stompClient = Stomp.over(socket);

  stompClient.connect({}, function () {
    updateStatus('dockerStatus', 'WebSocket 연결 성공!', 'green check circle');
    showToast('WebSocket 연결 성공!');

    stompClient.subscribe('/topic/stats', function (message) {
      try {
        const dockerResponse = JSON.parse(message.body);

        if (!dockerResponse.dockerPsContainerDtos || dockerResponse.dockerPsContainerDtos.length === 0) {
          document.getElementById('dockerStatsContainer').innerHTML =
              '<div class="ui warning message">컨테이너가 없습니다.</div>';
          return;
        }

        updateDockerContainers(dockerResponse);
      } catch (error) {
        console.error('메시지 처리 중 에러:', error);
      }
    });
  }, function (error) {
    updateStatus('dockerStatus', `WebSocket 연결 실패: ${error}`, 'red exclamation circle');
    showToast('WebSocket 연결 실패!', 5000);
  });
}

function updateDockerContainers(dockerResponse) {
  const containerList = dockerResponse.dockerPsContainerDtos || [];
  const containerDiv = document.getElementById('dockerStatsContainer');

  if (!containerDiv) {
    console.error('dockerStatsContainer가 없습니다.');
    return;
  }

  containerDiv.innerHTML = '';

  containerList.forEach((container) => {
    const status = container.Status || container.State || 'Unknown';
    const statusColor = getStatusColor(status);
    const containerName = container.Names ? container.Names.replace(/^\//, '') : 'Unknown';

    const cardHTML = `
      <div class="ui ${statusColor} card">
        <div class="content">
          <div class="header">${containerName}</div>
          <div class="meta">
            <span class="ui ${statusColor} label">${status}</span>
          </div>
          <div class="description">
            <table class="ui very basic compact table">
              <tbody>
                <tr><td>ID</td><td>${container.ID || 'N/A'}</td></tr>
                <tr><td>Image</td><td>${container.Image || 'N/A'}</td></tr>
                <tr><td>Created</td><td>${container.CreatedAt || 'N/A'}</td></tr>
                <tr><td>Ports</td><td>${formatPorts(container.Ports)}</td></tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="extra content">
          <button class="ui basic green button">Start</button>
          <button class="ui basic red button">Stop</button>
        </div>
      </div>
    `;

    containerDiv.insertAdjacentHTML('beforeend', cardHTML);
  });
}

function getStatusColor(status) {
  if (!status) return 'grey';
  if (status.toLowerCase().includes('up')) return 'green';
  if (status.toLowerCase().includes('exited')) return 'red';
  if (status.toLowerCase().includes('created')) return 'yellow';
  if (status.toLowerCase().includes('restarting')) return 'orange';
  return 'grey';
}

function formatPorts(ports) {
  return ports ? ports.split(',').join('<br>') : '없음';
}

/**
 * updateStatus(elementId, message, iconClass)
 * @param {string} elementId - 업데이트할 HTML 요소 ID
 * @param {string} message   - 표시할 메시지
 * @param {string} iconClass - Semantic UI 아이콘 클래스
 */
function updateStatus(elementId, message, iconClass) {
  const element = document.getElementById(elementId);
  if (element) {
    element.innerHTML = `<i class="${iconClass}"></i> ${message}`;
  }
}
