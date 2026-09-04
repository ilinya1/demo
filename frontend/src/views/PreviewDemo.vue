<template>
  <div class="page">
    <div class="page-head">
      <h1>整体 UI 三套方案对照</h1>
      <p>三套风格并排渲染真实布局，在浏览器里直接对比。选定方向后据此统一改造现有系统。</p>
    </div>

    <div class="cols">
      <!-- A 清新学院风 -->
      <section class="theme-a mock">
        <div class="mock-top">
          <span class="chip">A · 清新学院风</span>
          <span class="sub">青柠绿 · 米白 · 衬线标题 · 柔圆角</span>
        </div>
        <div class="frame">
          <aside class="sb">
            <div class="logo">宿</div>
            <div class="m-item on">仪表盘</div>
            <div class="m-item">学生</div>
            <div class="m-item">房间</div>
            <div class="m-item">卫生</div>
          </aside>
          <div class="body">
            <div class="t">仪表盘</div>
            <div class="cards">
              <div class="stat"><b>1,286</b><span>在校学生</span></div>
              <div class="stat"><b>6</b><span>宿舍楼栋</span></div>
              <div class="stat"><b>86.5%</b><span>入住率</span></div>
            </div>
            <div class="panel">
              <span class="pt">各楼栋入住率</span>
              <div class="barrow"><i></i></div>
              <div class="barrow" style="--w:60%"><i></i></div>
              <div class="barrow" style="--w:80%"><i></i></div>
            </div>
          </div>
        </div>
      </section>

      <!-- B 现代专业深色 -->
      <section class="theme-b mock">
        <div class="mock-top">
          <span class="chip">B · 现代专业深色</span>
          <span class="sub">玻璃侧边栏 · 靛蓝 · 细腻阴影</span>
        </div>
        <div class="frame">
          <aside class="sb">
            <div class="logo">宿</div>
            <div class="m-item on">仪表盘</div>
            <div class="m-item">学生</div>
            <div class="m-item">房间</div>
            <div class="m-item">卫生</div>
          </aside>
          <div class="body">
            <div class="t">仪表盘</div>
            <div class="cards">
              <div class="stat"><b>1,286</b><span>在校学生</span></div>
              <div class="stat"><b>6</b><span>宿舍楼栋</span></div>
              <div class="stat"><b>86.5%</b><span>入住率</span></div>
            </div>
            <div class="panel">
              <span class="pt">各楼栋入住率</span>
              <div class="barrow"><i></i></div>
              <div class="barrow" style="--w:60%"><i></i></div>
              <div class="barrow" style="--w:80%"><i></i></div>
            </div>
          </div>
        </div>
      </section>

      <!-- C 极简编辑风 -->
      <section class="theme-c mock">
        <div class="mock-top">
          <span class="chip">C · 极简编辑风</span>
          <span class="sub">大留白 · 黑白 · 大字号 · 强排版</span>
        </div>
        <div class="frame">
          <aside class="sb">
            <div class="logo">宿</div>
            <div class="m-item on">仪表盘</div>
            <div class="m-item">学生</div>
            <div class="m-item">房间</div>
            <div class="m-item">卫生</div>
          </aside>
          <div class="body">
            <div class="t">仪表盘</div>
            <div class="cards">
              <div class="stat"><b>1,286</b><span>在校学生</span></div>
              <div class="stat"><b>6</b><span>宿舍楼栋</span></div>
              <div class="stat"><b>86.5%</b><span>入住率</span></div>
            </div>
            <div class="panel">
              <span class="pt">各楼栋入住率</span>
              <div class="barrow"><i></i></div>
              <div class="barrow" style="--w:60%"><i></i></div>
              <div class="barrow" style="--w:80%"><i></i></div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page { padding: 32px 40px 56px; background: #fff; min-height: 100vh; box-sizing: border-box; }
.page-head h1 { margin: 0 0 6px; font-size: 22px; }
.page-head p { margin: 0 0 24px; color: #888; font-size: 13px; }
.cols { display: grid; grid-template-columns: repeat(3, 1fr); gap: 28px; }
.mock { }
.mock-top { display: flex; align-items: baseline; gap: 10px; margin-bottom: 12px; }
.chip { font-weight: 700; font-size: 15px; }
.sub { color: #999; font-size: 12px; }

/* ---------- 主题 A：清新学院风 ---------- */
.theme-a { --accent: #3f9d5f; --accent-soft: #e8f3ec; --bg: #f7f4ee; --surface: #fff; --ink: #2c3a33; --muted: #7d8a83; --radius: 14px; --font-title: "KaiTi", "STKaiti", "SimSun", serif; }
.theme-a .mock-top { color: var(--ink); }

/* ---------- 主题 B：现代专业深色 ---------- */
.theme-b { --accent: #4f6ef7; --accent-soft: #e9edff; --bg: #f4f6fb; --surface: #fff; --ink: #1c2340; --muted: #6b7290; --radius: 12px; --sb-bg: rgba(17,24,39,.92); --sb-ink: #b6bdc8; --font-title: "Microsoft YaHei", sans-serif; }
.theme-b .mock-top { color: var(--ink); }

/* ---------- 主题 C：极简编辑风 ---------- */
.theme-c { --accent: #e4572e; --accent-soft: #fdeee7; --bg: #fbfbfa; --surface: #fff; --ink: #141414; --muted: #8c8c8c; --radius: 4px; --font-title: "SimHei", "Microsoft YaHei", sans-serif; }
.theme-c .mock-top { color: var(--ink); }

/* ggg frame + mock 面板 */
.frame { border-radius: var(--radius); overflow: hidden; background: var(--bg); border: 1px solid rgba(0,0,0,.06); box-shadow: 0 8px 24px rgba(0,0,0,.06); display: flex; min-height: 380px; }
.theme-a .frame { font-family: var(--font-title); }
.theme-b .frame, .theme-c .frame { font-family: sans-serif; }

.sb { width: 64px; padding-top: 18px; }
.theme-a .sb, .theme-c .sb { background: var(--surface); border-right: 1px solid rgba(0,0,0,.06); }
.theme-b .sb { background: var(--sb-bg); backdrop-filter: blur(6px); }
.sb .logo { width: 30px; height: 30px; margin: 0 auto 18px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.theme-a .sb .logo { background: var(--accent); color: #fff; font-size: 14px; }
.theme-b .sb .logo { background: var(--accent); color: #fff; font-size: 14px; }
.theme-c .sb .logo { border: 1.5px solid var(--ink); color: var(--ink); font-size: 13px; background: transparent; }
.m-item { margin: 0 12px 8px; padding: 7px 0; text-align: center; font-size: 11px; border-radius: 8px; }
.theme-a .m-item, .theme-c .m-item { color: var(--muted); }
.theme-b .m-item { color: var(--sb-ink); }
.m-item.on { color: #fff; background: var(--accent); }
.theme-c .m-item.on { color: var(--ink); background: var(--accent); font-weight: 700; }

.body { flex: 1; padding: 18px 18px 22px; }
.t { font-size: 19px; font-weight: 800; color: var(--ink); margin-bottom: 14px; letter-spacing: .5px; }
.cards { display: grid; grid-template-columns: repeat(3,1fr); gap: 10px; margin-bottom: 12px; }
.stat { background: var(--surface); border-radius: var(--radius); padding: 12px 8px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.stat b { display: block; font-size: 19px; color: var(--ink); font-weight: 800; }
.stat span { font-size: 11px; color: var(--muted); }
.panel { background: var(--surface); border-radius: var(--radius); padding: 12px 14px; box-shadow: 0 2px 8px rgba(0,0,0,.04); }
.pt { display: block; font-size: 12px; color: var(--muted); margin-bottom: 10px; }
.barrow { height: 9px; background: var(--accent-soft); border-radius: 5px; margin-bottom: 8px; overflow: hidden; }
.barrow i { display: block; height: 100%; width: 100%; background: var(--accent); border-radius: 5px; }
.barrow:nth-child(3) i { width: var(--w); }
.barrow:nth-child(4) i { width: var(--w); }
</style>