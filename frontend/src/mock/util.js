export const ok = (data) => ({ code: 0, msg: 'success', data })
export const fail = (msg) => ({ code: 1, msg })