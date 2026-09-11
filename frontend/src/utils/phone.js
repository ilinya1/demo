// 手机号规范化校验：仅 11 位中国大陆手机号，与后端 PhoneUtils 规则一致。
const MOBILE_RE = /^1[3-9]\d{9}$/

/** 校验是否为合法手机号（去除间隔符后匹配） */
export function isMobilePhone(value) {
  if (value === null || value === undefined) return false
  return MOBILE_RE.test(String(value).replace(/[\s\-()（）+.]/g, ''))
}

/** 规范化为纯数字（仅用于展示/提交前预览） */
export function normalizePhone(value) {
  if (value === null || value === undefined) return ''
  return String(value).replace(/[\s\-()（）+.]/g, '')
}

/**
 * Element Plus 表单校验：必填 + 手机号格式。
 * @param {string} label 字段中文名，用于错误提示，如「联系电话」
 */
export function mobileRequired(label = '电话') {
  return (rule, value, callback) => {
    if (value === null || value === undefined || !String(value).trim()) {
      callback(new Error(`${label}不能为空`))
    } else if (!isMobilePhone(value)) {
      callback(new Error(`${label}格式不正确，请输入 11 位手机号`))
    } else {
      callback()
    }
  }
}