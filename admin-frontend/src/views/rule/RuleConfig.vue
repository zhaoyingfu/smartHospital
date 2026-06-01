<template>
  <div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="ruleKey" label="规则键" />
        <el-table-column prop="ruleValue" label="当前值" />
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="scope" label="范围">
          <template #default="{ row }">{{ row.scope === 'GLOBAL' ? '全局' : '设备级' }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button text type="primary" @click="editRule(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showEditDialog" title="编辑规则" width="400px">
      <el-form>
        <el-form-item label="规则">{{ editingRule?.ruleKey }}</el-form-item>
        <el-form-item label="说明">{{ editingRule?.description }}</el-form-item>
        <el-form-item label="值">
          <el-input v-model="editValue" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRule">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref<any[]>([])
const showEditDialog = ref(false)
const editingRule = ref<any>(null)
const editValue = ref('')

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/rule/list')
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function editRule(row: any) {
  editingRule.value = row
  editValue.value = row.ruleValue
  showEditDialog.value = true
}

async function saveRule() {
  await request.put(`/admin/rule/${editingRule.value.id}`, null, { params: { ruleValue: editValue.value } })
  ElMessage.success('保存成功')
  showEditDialog.value = false
  loadData()
}

onMounted(loadData)
</script>
