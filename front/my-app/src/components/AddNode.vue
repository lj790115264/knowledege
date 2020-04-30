<template>
  <div class="hello">
    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-input type="input" autosize placeholder="知识点" v-model="name"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="addNode()">添加</el-button>
      </el-col>
    </el-row>
    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-input type="input" autosize placeholder="搜索类似知识点" v-model="content"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="querySearchAsync()">查询</el-button>
      </el-col>
    </el-row>
    <el-table
      :data="tableData"
      style="width: 100%">
        <el-table-column
          prop="name"
          label="名称"
          width="180">
        </el-table-column>
      </el-table>
  </div>
</template>

<script>
const axios = require("axios");
export default {
  name: 'HelloWorld',
  props: {
    msg: String
  },
  data() {
    return {
      name: "",
      content: "",
      tableData: []
    };
  },
  methods: {
    querySearchAsync() {
      if (!this.content) {
        this.tableData = []
        return;
      }

      axios
        .post("http://localhost:8089/node/list", {
          content: this.content
        })
        .then((response) => {
          this.tableData = response.data
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    addNode() {
      if ((this.name == "")) {
        return;
      }
      axios
        .post("http://localhost:8089/node/add", {
          name: this.name
        })
        .then((response) => {
          this.name = "";
          console.log(response);
          this.querySearchAsync()
        })
        .catch(function(error) {
          console.log(error);
        });
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
.row-bg {
  margin-bottom: 10px;
}
</style>
