export const getOrCreateDeviceId = (): string => {
  const DEVICE_ID_KEY = 'device_id';
  let deviceId = localStorage.getItem(DEVICE_ID_KEY);

  // 로컬 스토리지에 없으면 최초 1회 생성하여 저장
  if (!deviceId) {
    deviceId = crypto.randomUUID();
    localStorage.setItem(DEVICE_ID_KEY, deviceId);
  }

  return deviceId;
};
