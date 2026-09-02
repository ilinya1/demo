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