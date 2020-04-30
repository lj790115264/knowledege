<template>
  <div class="hello">
    <el-row type="flex" class="row-bg" justify="left">
      <el-col :span="12">
        <el-input type="input" autosize placeholder="标题" v-model="title"></el-input>
      </el-col>
    </el-row>

    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-input type="input" autosize placeholder="地址" v-model="url"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="addArticle()">添加</el-button>
      </el-col>
    </el-row>

    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-input type="input" autosize placeholder="搜索文章" v-model="content"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="querySearchAsync()">查询</el-button>
      </el-col>
    </el-row>
    <el-table
      :data="tableData"
      style="width: 100%">
        <el-table-column
          prop="title"
          label="标题"
          width="180">
        </el-table-column>
        <el-table-column
          prop="url"
          label="地址">
        </el-table-column>
      </el-table>
  </div>
</template>

<script>
const axios = require("axios");

export default {
  name: "HelloWorld",
  data() {
    return {
      content: "",
      title: "",
      url: "",
      tableData: []
    };
  },
  props: {
    msg: String
  },
  methods: {
    querySearchAsync() {
      if (!this.content) {
        this.tableData = []
        return;
      }

      axios
        .post("http://localhost:8089/article/list", {
          content: this.content
        })
        .then((response) => {
          this.tableData = response.data
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    addArticle() {
      if ((this.title == "")) {
        return;
      }
      axios
        .post("http://localhost:8089/article/add", {
          title: this.title,
          url: this.url
        })
        .then((response) => {
          this.title = "";
          this.url = "";
          console.log(response);
        })
        .catch(function(error) {
          console.log(error);
        });
    }
  }
};
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
