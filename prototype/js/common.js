// ===== 学生宿舍管理系统 原型图公共脚本 =====

// 弹窗控制
function openModal(id) {
    var modal = document.getElementById(id);
    if (modal) {
        modal.classList.add('show');
    }
}

function closeModal(id) {
    var modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('show');
    }
}

// 点击遮罩层关闭弹窗
document.addEventListener('click', function (e) {
    if (e.target.classList && e.target.classList.contains('modal-mask')) {
        e.target.classList.remove('show');
    }
});

// 菜单高亮：根据当前页面文件名自动高亮对应菜单
document.addEventListener('DOMContentLoaded', function () {
    var currentPage = window.location.pathname.split('/').pop();
    var menuItems = document.querySelectorAll('.menu a.menu-item');
    menuItems.forEach(function (item) {
        var href = item.getAttribute('href');
        if (href && href.split('/').pop() === currentPage) {
            item.classList.add('active');
        }
    });
});

// 模拟提示（原型图用 alert 简化）
function showMsg(msg) {
    alert(msg);
}

// ===== CSV 导出（前端生成并下载，含 UTF-8 BOM，Excel 可直接打开不乱码）=====
// selector: 表格选择器；filename: 下载文件名；skipLastColumn: 是否跳过最后一列（通常是“操作”列）
function exportTableCSV(selector, filename, skipLastColumn) {
    var tbl = document.querySelector(selector);
    if (!tbl) {
        showMsg('未找到表格：' + selector);
        return;
    }
    var lines = [];
    tbl.querySelectorAll('tr').forEach(function (tr) {
        var cells = Array.prototype.slice.call(tr.querySelectorAll('th,td'));
        if (skipLastColumn && cells.length) {
            cells = cells.slice(0, -1);
        }
        var vals = cells.map(function (c) {
            var t = (c.textContent || '').trim();
            // 含逗号/引号/换行时用双引号包裹，并转义内部引号
            if (/[",\r\n]/.test(t)) {
                t = '"' + t.replace(/"/g, '""') + '"';
            }
            return t;
        });
        lines.push(vals.join(','));
    });
    var csv = '\uFEFF' + lines.join('\r\n'); // \uFEFF = UTF-8 BOM
    var blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    var url = URL.createObjectURL(blob);
    var a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
    showMsg('已导出：' + filename);
}