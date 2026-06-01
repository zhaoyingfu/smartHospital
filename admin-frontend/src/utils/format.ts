export function formatAmount(fen: number): string {
  return (fen / 100).toFixed(2)
}

export function formatStatus(status: string): string {
  const map: Record<string, string> = {
    PENDING: '待支付',
    PROCESSING: '支付中',
    SUCCESS: '成功',
    FAILED: '失败',
    CLOSED: '已关闭',
    REFUNDED: '已退款',
    PARTIAL_REFUND: '部分退款',
    PENDING_APPROVE: '待审批',
    APPROVED: '已审批',
    REFUNDING: '退款中',
    MATCHED: '匹配',
    SYSTEM_ONLY: '系统有渠道无',
    CHANNEL_ONLY: '渠道有系统无',
    AMOUNT_MISMATCH: '金额不符',
    UNHANDLED: '未处理',
    HANDLED: '已处理',
    ONLINE: '在线',
    OFFLINE: '离线',
    MAINTENANCE: '维护中',
  }
  return map[status] || status
}
