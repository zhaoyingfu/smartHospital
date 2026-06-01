<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2 class="page-title">选择科室</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="category-tabs">
        <button
          v-for="cat in categories"
          :key="cat"
          class="tab-btn"
          :class="{ active: activeCategory === cat }"
          @click="activeCategory = cat"
        >
          {{ cat }}
        </button>
      </div>
      <div class="dept-grid">
        <button
          v-for="dept in filteredDepts"
          :key="dept.id"
          class="dept-btn"
          @click="selectDept(dept)"
        >
          <div class="dept-icon">
            <el-icon :size="24"><FolderOpened /></el-icon>
          </div>
          <span class="dept-name">{{ dept.name }}</span>
        </button>
        <div v-if="filteredDepts.length === 0" class="empty-state">
          <el-empty description="暂无科室" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const departments = ref<any[]>([])
const activeCategory = ref('内科')

const categories = ['内科', '外科', '妇儿', '专科', '中医']

const filteredDepts = computed(() =>
  departments.value.filter(d => d.category === activeCategory.value)
)

async function loadDepartments() {
  const res: any = await request.get('/kiosk/registration/departments')
  departments.value = res.data || []
}

function selectDept(dept: any) {
  router.push({ path: '/registration/doctor', query: { departmentId: dept.id, departmentName: dept.name } })
}

onMounted(loadDepartments)
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; padding: 28px 32px; overflow-y: auto; }

.category-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}

.tab-btn {
  padding: 10px 28px;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  background: white;
  color: var(--text-secondary);
  font-family: inherit;
  transition: all 0.2s ease;
  box-shadow: var(--shadow);
}

.tab-btn:hover { color: var(--primary); }
.tab-btn.active {
  background: var(--primary);
  color: white;
  box-shadow: 0 4px 12px color-mix(in srgb, var(--primary) 30%, transparent);
}

.dept-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.dept-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 28px 16px;
  background: white;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow);
  font-family: inherit;
}

.dept-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.dept-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--primary-bg);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.dept-name {
  font-size: 16px;
  font-weight: 500;
  color: var(--text);
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
}
</style>
