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
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="name" label="名称" width="180"></el-table-column>
      <el-table-column prop="name" label fixed="right" width="180">
        <template slot-scope="scope">
          <el-button @click="handleClickNote(scope.row)" type="text" size="small">注解</el-button>
          <el-button @click="handleClickArticle(scope.row)" type="text" size="small">文章</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="注解" :visible.sync="dialogFormVisible">
      <el-table :data="noteListData">
        <el-table-column property="content" label width="600"></el-table-column>
      </el-table>
      <el-button @click="addNote()" type="primary" size="small">添加注解</el-button>
    </el-dialog>

    <el-dialog title="文章" :visible.sync="articleDialogFormVisible">
      <el-row type="flex" class="row-bg" justify="space-between">
        <el-col :span="8">
          <el-input type="input" autosize placeholder="搜索文章" v-model="articleContent"></el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="querySearchAsyncArticle()">查询</el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="addArticle()" type="primary" size="small">添加文章</el-button>
        </el-col>
      </el-row>

      <el-table
        :data="articleTableData"
        highlight-current-row
        @current-change="handleCurrentChange"
        style="width: 100%"
      >
        <el-table-column prop="title" label="标题" width="180"></el-table-column>
        <el-table-column prop="url" label="地址"></el-table-column>
      </el-table>

      <el-table :data="articleData">
        <el-table-column prop="title" label="标题" width="180"></el-table-column>
        <el-table-column prop="url" label="地址"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
const axios = require("axios");
export default {
  name: "HelloWorld",
  props: {
    msg: String
  },
  data() {
    return {
      addNoteContent: "",
      name: "",
      content: "",
      dialogFormVisible: false,
      tableData: [],
      noteListData: [],
      dialogRow: "",
      articleDialogFormVisible: false,
      articleData: [],
      articleContent: "",
      selectedArticle: {},
      articleTableData: []
    };
  },
  methods: {
    handleCurrentChange(val) {
      this.selectedArticle = val;
    },
    // 新增节点注解
    addNote() {
      this.$prompt("请输入注解", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消"
      })
        .then(({ value }) => {
          var obj = {
            id: this.dialogRow.id,
            content: value,
            // 1 node 0 relation
            type: 1
          };
          axios
            .post("http://localhost:8089/node/note/relation/add", obj)
            .then(() => {
              this.noteList();
            })
            .catch(function(error) {
              console.log(error);
            });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "取消输入"
          });
        });
    },
    handleClickNote(row) {
      this.dialogRow = row;
      this.dialogFormVisible = true;
      this.noteList();
    },
    handleClickArticle(row) {
      this.articleDialogFormVisible = true;
      this.dialogRow = row;
      this.articleList()
    },
    // 节点添加文章关联
    addArticle() {
      if (!this.selectedArticle || !this.selectedArticle.id) {
        this.$message("未选中文章");
        return;
      }
      axios
        .post("http://localhost:8089/article/relate", {
          articleId: this.selectedArticle.id,
          nodeId: this.dialogRow.id,
          type: 1,
        })
        .then(() => {
          this.articleList();
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    articleList() {
      var obj = {
        type: 1,
        id: this.dialogRow.id
      };

      axios
        .post("http://localhost:8089/article/node/articles", obj)
        .then(response => {
          this.articleData = response.data.articles;
          this.$forceUpdate();
        })
        .catch(function(error) {
          console.log(error);
        });
    },

    querySearchAsyncArticle() {
      if (!this.articleContent) {
        this.articleTableData = [];
        return;
      }

      axios
        .post("http://localhost:8089/article/list", {
          content: this.articleContent
        })
        .then(response => {
          this.articleTableData = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },

    noteList() {
      var obj = {
        type: 1,
        id: this.dialogRow.id
      };
      axios
        .post("http://localhost:8089/node/note/relation", obj)
        .then(response => {
          this.noteListData = response.data;
          this.$forceUpdate();
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    querySearchAsync() {
      if (!this.content) {
        this.tableData = [];
        return;
      }

      axios
        .post("http://localhost:8089/node/list", {
          content: this.content
        })
        .then(response => {
          this.tableData = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    addNode() {
      if (this.name == "") {
        return;
      }
      axios
        .post("http://localhost:8089/node/add", {
          name: this.name
        })
        .then(response => {
          this.name = "";
          console.log(response);
          this.querySearchAsync();
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
