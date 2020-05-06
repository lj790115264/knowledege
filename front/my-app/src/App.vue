<template>
  <el-container>
    <el-header></el-header>
    <el-main>

      <el-row type="flex" class="row-bg" justify="center">
        <el-col :span="12">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="新增文章" name="first">
              <AddArticle></AddArticle>
            </el-tab-pane>
            <el-tab-pane label="新增知识点" name="second">
              <AddNode></AddNode>
            </el-tab-pane>
            <el-tab-pane label="关系新增" name="third">
              <AddRelation></AddRelation>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script>
import AddArticle from "./components/AddArticle.vue";
import AddNode from "./components/AddNode.vue";
import AddRelation from "./components/AddRelation.vue";

export default {
  name: "app",
  data() {
    return {
      activeName: "first"
    };
  },
  methods: {
    handleClick() {}
  },
  components: {
    AddArticle,
    AddNode,
    AddRelation
  }
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
