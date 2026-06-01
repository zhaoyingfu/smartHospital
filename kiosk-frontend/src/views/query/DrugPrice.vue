<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2 class="page-title">药品价格查询</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="输入药品名称或拼音码" size="large" class="search-input" clearable @keyup.enter="search" />
        <el-button type="primary" size="large" round @click="search">
          <el-icon><Search /></el-icon> 查询
        </el-button>
      </div>
      <div v-if="drugs.length" class="table-wrap" style="margin-top: 20px">
        <el-table :data="drugs" stripe size="large" style="width: 100%">
          <el-table-column prop="name" label="药品名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="category" label="分类" />
          <el-table-column label="单价">
            <template #default="{ row }"><span class="price">¥{{ formatAmount(row.unitPrice) }}</span></template>
          </el-table-column>
        </el-table>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'

const keyword = ref('')
const drugs = ref<any[]>([])

async function search() {
  if (!keyword.value) return
  const res: any = await request.get('/kiosk/query/drugs', { params: { keyword: keyword.value } })
  drugs.value = res.data || []
}
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; padding: 28px 32px; overflow-y: auto; }

.search-bar {
  display: flex;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.search-input { flex: 1; }
.search-input :deep(.el-input__wrapper) { height: 48px; }

.price { color: var(--danger); font-weight: 600; }

.table-wrap { background: white; border-radius: 16px; padding: 4px; box-shadow: var(--shadow); }
</style>
