// 方法一
// export function formatDate(val) {
//     var data = new Date(Number(val) * 1000);   // 时间戳为10位需*1000， 时间戳为13位的话，不需要*1000
//     var Y = data.getFullYear() + "-";
//     var M = (data.getMonth() + 1 < 10 ? "0" + (data.getMonth() + 1) : data.getMonth() + 1) + "-";
//     var D = data.getDate() + " ";
//     var h = data.getHours() + ":";
//     var m = data.getMinutes() + ":";
//     var s = (data.getSeconds() < 10 ? "0" + (data.getSeconds()) : data.getSeconds());
//     return Y + M + D + h + m + s;
// }

export function formatDate(val) {
    const date = new Date(val);

    const Y = date.getFullYear();
    const M = String(date.getMonth() + 1).padStart(2, '0');
    const D = String(date.getDate()).padStart(2, '0');
    const h = String(date.getHours()).padStart(2, '0');
    const m = String(date.getMinutes()).padStart(2, '0');
    const s = String(date.getSeconds()).padStart(2, '0');

    return `${Y}-${M}-${D} ${h}:${m}:${s}`;
}