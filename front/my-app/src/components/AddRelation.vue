<template>
  <div class="hello">
    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-select
          v-model="form.masterId"
          filterable
          remote
          reserve-keyword
          placeholder="父节点"
          :remote-method="remoteMaterNode"
          :loading="masterLoading"
        >
          <el-option
            v-for="item in masterNodeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="8">
        <el-button type="primary" @click="relationList()">查询已有关系</el-button>
      </el-col>
    </el-row>
    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-select
          v-model="form.child"
          filterable
          multiple
          remote
          reserve-keyword
          placeholder="子节点"
          :remote-method="remoteChildNode"
          :loading="childLoading"
        >
          <el-option
            v-for="item in childNodeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>

    <el-row type="flex" class="row-bg" justify="space-between">
      <el-col :span="8">
        <el-input type="input" autosize placeholder="关系" v-model="form.relation"></el-input>
      </el-col>
      <el-col :span="8">
        <el-input type="input" autosize placeholder="关系备注" v-model="form.relationRemark"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="addRelation()">添加</el-button>
      </el-col>
    </el-row>

    <!-- <el-row type="flex" class="row-bg"> -->
    <el-collapse v-show="masterRelations.length > 0">
      <el-collapse-item
        :title="relation.relation + ':' + relation.relationRemark"
        v-for="relation in masterRelations"
        :key="relation.relationId"
        name="relation.relationId"
      >
        <div>
          <el-button type="text" @click="editRelation(relation)">添加到该关系中</el-button>
          <el-button type="text" @click="articleList(relation)">查看文章</el-button>
          <el-button type="text" @click="noteList(relation)">查看注解列表</el-button>
          <el-button type="text" @click="addNote(relation)">添加注解</el-button>
        </div>
        <div>
          <span
            class="text item"
            v-for="child in relation.list"
            :key="child.relationId"
          >{{child.nodeName}}</span>
        </div>
        <div v-for="note in relation.noteList" :key="note.id">{{note.content}}</div>
        <!-- <el-table
          :data="relation"
          highlight-current-row
          @current-change="handleCurrentChange"
          style="width: 100%"
        >
          <el-table-column prop="title" label="标题" width="180"></el-table-column>
          <el-table-column prop="url" label="地址"></el-table-column>
        </el-table>-->
      </el-collapse-item>
    </el-collapse>

    <el-dialog title="文章" :visible.sync="articleDialogFormVisible">
      <el-row type="flex" class="row-bg" justify="space-between">
        <el-col :span="8">
          <el-input type="input" autosize placeholder="搜索文章" v-model="articleContent"></el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="querySearchAsync()">查询</el-button>
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

      <el-table :data="relationData.articles">
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
  data() {
    return {
      articleDialogFormVisible: false,
      relationData: {},
      form: {
        masterId: "",
        relation: "",
        relationId: "",
        relationRemark: "",
        child: []
      },
      masterNodeList: [],
      childNodeList: [],
      masterLoading: false,
      childLoading: false,
      masterRelations: [],
      articleContent: "",
      articleTableData: [],
      selectedArticle: {}
    };
  },
  props: {
    msg: String
  },
  methods: {
    handleCurrentChange(val) {
      this.selectedArticle = val;
    },
    querySearchAsync() {
      if (!this.articleContent) {
        this.articleTableData = [];
        return;
      }

      axios
        .post("http://192.168.2.105:8089/article/list", {
          content: this.articleContent
        })
        .then(response => {
          this.articleTableData = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    articleList(relation) {
      this.articleDialogFormVisible = true;
      this.relationData = relation;
    },
    // 关系添加文章关联
    addArticle() {
      let relation = this.relationData.relationId;
      if (!this.selectedArticle || !this.selectedArticle.id) {
        this.$message("未选中文章");
        return;
      }
      axios
        .post("http://192.168.2.105:8089/article/relate", {
          articleId: this.selectedArticle.id,
          nodeId: relation,
          type: 0,
        })
        .then(() => {
          this.relationList()
          this.articleDialogFormVisible = false;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    remoteMaterNode(query) {
      axios
        .post("http://192.168.2.105:8089/node/list", {
          content: query
        })
        .then(response => {
          this.masterNodeList = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    remoteChildNode(query) {
      axios
        .post("http://192.168.2.105:8089/node/list", {
          content: query
        })
        .then(response => {
          this.childNodeList = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    addRelation() {
      var obj = {
        id: this.form.masterId,
        relation: this.form.relation,
        relationRemark: this.form.relationRemark,
        child: this.form.child
      };

      axios
        .post("http://192.168.2.105:8089/node/add/relation", obj)
        .then(response => {
          this.childNodeList = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    editRelation(relation) {
      var obj = {
        id: this.form.masterId,
        relationId: relation.relationId,
        relation: relation.relation,
        child: this.form.child
      };

      axios
        .post("http://192.168.2.105:8089/node/add/relation", obj)
        .then(response => {
          this.childNodeList = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    noteList(relation) {
      var obj = {
        type: 0,
        id: relation.relationId
      };
      axios
        .post("http://192.168.2.105:8089/node/note/relation", obj)
        .then(response => {
          relation.noteList = response.data;
          this.$forceUpdate();
          console.log(relation);
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    addNote(relation) {
      this.$prompt("请输入注解", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消"
      })
        .then(({ value }) => {
          var obj = {
            id: relation.relationId,
            content: value,
            // 1 node 0 relation
            type: 0
          };
          axios
            .post("http://192.168.2.105:8089/node/note/relation/add", obj)
            .then(() => {
              this.noteList(relation);
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
    relationList() {
      var obj = {
        id: this.form.masterId
      };
      axios
        .post("http://192.168.2.105:8089/node/relations", obj)
        .then(response => {
          this.masterRelations = response.data;
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
.item {
  margin-right: 10px;
}
</style>
