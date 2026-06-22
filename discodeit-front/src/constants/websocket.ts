const WS_PREFIX = {
  SUB: '/sub',
  PUB: '/pub',
}

export const WS_DESTINATIONS = {
  SUB_MESSAGE: (channelId: string) => `${WS_PREFIX.SUB}/channels/${channelId}/messages`,
  SUB_PUBLIC_CHANNEL: `${WS_PREFIX.SUB}/channels`,
  SUB_PRIVATE_CHANNEL: (userId: string) => `${WS_PREFIX.SUB}/users/${userId}/channels`,
  SUB_ALL_USER: `${WS_PREFIX.SUB}/users`,
  SUB_NOTIFICATION: (userId: string) => `${WS_PREFIX.SUB}/users/${userId}/notifications`,

  PUB_MESSAGE: (channelId: string) => `${WS_PREFIX.PUB}/channels/${channelId}/messages`,
};
