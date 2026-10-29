/* METADATA
{
    "name": "extended_file_tools",

    "display_name": {
        "zh": "增强文件工具",
        "en": "Extended File Tools", "ar": "أدوات الملفات الموسّعة"
    },
    "description": {
        "zh": "拓展文件工具包：提供 file_exists / move_file / copy_file / file_info / unzip_files / zip_files / open_file / share_file（默认文件工具中已移除这些项）。",
        "en": "Extended file tools: file_exists / move_file / copy_file / file_info / unzip_files / zip_files / open_file / share_file (removed from default file tools).", "ar": "حزمة أدوات الملفات الموسّعة: توفّر file_exists / move_file / copy_file / file_info / unzip_files / zip_files / open_file / share_file (أُزيلت هذه العناصر من أدوات الملفات الافتراضية)."
    },
    "category": "File",
    "enabledByDefault": true,
    "tools": [
        {
            "name": "file_exists",
            "description": { "zh": "检查文件或目录是否存在。", "en": "Check if a file or directory exists.", "ar": "التحقّق من وجود ملف أو مجلد." },
            "parameters": [
                { "name": "path", "description": { "zh": "目标路径", "en": "Target path", "ar": "المسار الهدف" }, "type": "string", "required": true },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false }
            ]
        },
        {
            "name": "move_file",
            "description": { "zh": "移动或重命名文件/目录。", "en": "Move or rename a file/directory.", "ar": "نقل أو إعادة تسمية ملف/مجلد." },
            "parameters": [
                { "name": "source", "description": { "zh": "源路径", "en": "Source path", "ar": "المسار المصدر" }, "type": "string", "required": true },
                { "name": "destination", "description": { "zh": "目标路径", "en": "Destination path", "ar": "المسار الهدف" }, "type": "string", "required": true },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false }
            ]
        },
        {
            "name": "copy_file",
            "description": { "zh": "复制文件/目录（支持跨环境复制）。", "en": "Copy a file/directory (supports cross-environment copy).", "ar": "نسخ ملف/مجلد (يدعم النسخ بين البيئات)." },
            "parameters": [
                { "name": "source", "description": { "zh": "源路径", "en": "Source path", "ar": "المسار المصدر" }, "type": "string", "required": true },
                { "name": "destination", "description": { "zh": "目标路径", "en": "Destination path", "ar": "المسار الهدف" }, "type": "string", "required": true },
                { "name": "recursive", "description": { "zh": "可选：是否递归，默认 false", "en": "Optional: recursive (default: false)", "ar": "اختياري: النسخ التكراري، الافتراضي false" }, "type": "boolean", "required": false },
                { "name": "source_environment", "description": { "zh": "可选：源环境 android/linux", "en": "Optional: source environment android/linux", "ar": "اختياري: البيئة المصدر android/linux" }, "type": "string", "required": false },
                { "name": "dest_environment", "description": { "zh": "可选：目标环境 android/linux", "en": "Optional: destination environment android/linux", "ar": "اختياري: البيئة الهدف android/linux" }, "type": "string", "required": false }
            ]
        },
        {
            "name": "file_info",
            "description": { "zh": "获取文件/目录信息。", "en": "Get file/directory info.", "ar": "الحصول على معلومات الملف/المجلد." },
            "parameters": [
                { "name": "path", "description": { "zh": "目标路径", "en": "Target path", "ar": "المسار الهدف" }, "type": "string", "required": true },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false }
            ]
        },
        {
            "name": "zip_files",
            "description": { "zh": "压缩文件/目录。", "en": "Zip files/directories.", "ar": "ضغط الملفات/المجلدات." },
            "parameters": [
                { "name": "source", "description": { "zh": "源路径", "en": "Source path", "ar": "المسار المصدر" }, "type": "string", "required": true },
                { "name": "destination", "description": { "zh": "输出 zip 文件路径", "en": "Destination zip path", "ar": "مسار ملف zip الناتج" }, "type": "string", "required": true },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false },
                { "name": "include_root_directory", "description": { "zh": "压缩目录时是否保留源目录名作为顶层目录，默认 true", "en": "When zipping a directory, keep the source directory name as the top-level folder, default true", "ar": "عند ضغط مجلد، احتفظ باسم المجلد المصدر كدليل أعلى، الافتراضي true" }, "type": "boolean", "required": false }
            ]
        },
        {
            "name": "unzip_files",
            "description": { "zh": "解压 zip 文件。", "en": "Unzip an archive.", "ar": "فك ضغط أرشيف zip." },
            "parameters": [
                { "name": "source", "description": { "zh": "zip 文件路径", "en": "Zip file path", "ar": "مسار ملف zip" }, "type": "string", "required": true },
                { "name": "destination", "description": { "zh": "解压目录", "en": "Destination directory", "ar": "دليل فك الضغط" }, "type": "string", "required": true },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false }
            ]
        },
        {
            "name": "open_file",
            "description": { "zh": "用系统默认应用打开文件。", "en": "Open a file with system default app.", "ar": "فتح ملف بالتطبيق الافتراضي للنظام." },
            "parameters": [
                { "name": "path", "description": { "zh": "文件路径", "en": "File path", "ar": "مسار الملف" }, "type": "string", "required": true },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false }
            ]
        },
        {
            "name": "share_file",
            "description": { "zh": "分享文件给其他应用。", "en": "Share a file with other apps.", "ar": "مشاركة ملف مع التطبيقات الأخرى." },
            "parameters": [
                { "name": "path", "description": { "zh": "文件路径", "en": "File path", "ar": "مسار الملف" }, "type": "string", "required": true },
                { "name": "title", "description": { "zh": "可选：分享标题", "en": "Optional: share title", "ar": "اختياري: عنوان المشاركة" }, "type": "string", "required": false },
                { "name": "environment", "description": { "zh": "可选：android/linux", "en": "Optional: android/linux", "ar": "اختياري: android/linux" }, "type": "string", "required": false }
            ]
        }
    ]
}*/
const ExtendedFileTools = (function () {
    async function file_exists(params) {
        const result = await Tools.Files.exists(params.path, params.environment);
        return { success: !!result && (result.exists ?? true), message: 'تم التحقّق من الوجود', data: result };
    }
    async function move_file(params) {
        const result = await Tools.Files.move(params.source, params.destination, params.environment);
        return { success: !!result, message: 'تم النقل', data: result };
    }
    async function copy_file(params) {
        const result = await Tools.Files.copy(params.source, params.destination, params.recursive, params.source_environment, params.dest_environment);
        return { success: !!result, message: 'تم النسخ', data: result };
    }
    async function file_info(params) {
        const result = await Tools.Files.info(params.path, params.environment);
        return { success: !!result, message: 'تم جلب المعلومات', data: result };
    }
    async function zip_files(params) {
        const result = await Tools.Files.zip(params.source, params.destination, params.environment, params.include_root_directory);
        return { success: !!result, message: 'تم الضغط', data: result };
    }
    async function unzip_files(params) {
        const result = await Tools.Files.unzip(params.source, params.destination, params.environment);
        return { success: !!result, message: 'تم فك الضغط', data: result };
    }
    async function open_file(params) {
        const result = await Tools.Files.open(params.path, params.environment);
        return { success: !!result, message: 'تم فتح الملف', data: result };
    }
    async function share_file(params) {
        const result = await Tools.Files.share(params.path, params.title, params.environment);
        return { success: !!result, message: 'تمت مشاركة الملف', data: result };
    }
    async function wrapToolExecution(func, params) {
        try {
            const result = await func(params);
            complete(result);
        }
        catch (error) {
            console.error(`Tool ${func.name} failed unexpectedly`, error);
            complete({
                success: false,
                message: `خطأ غير متوقع أثناء تنفيذ الأداة: ${error.message}`,
            });
        }
    }
    async function main() {
        const results = [];
        results.push({ tool: 'file_exists', result: { success: null, message: 'لم يتم الاختبار' } });
        results.push({ tool: 'move_file', result: { success: null, message: 'لم يتم الاختبار (سيتم نقل/إعادة تسمية الملف)' } });
        results.push({ tool: 'copy_file', result: { success: null, message: 'لم يتم الاختبار (سيتم نسخ الملف)' } });
        results.push({ tool: 'file_info', result: { success: null, message: 'لم يتم الاختبار' } });
        results.push({ tool: 'zip_files', result: { success: null, message: 'لم يتم الاختبار (سيتم كتابة ملف zip)' } });
        results.push({ tool: 'unzip_files', result: { success: null, message: 'لم يتم الاختبار (سيتم كتابة ملفات فك الضغط)' } });
        results.push({ tool: 'open_file', result: { success: null, message: 'لم يتم الاختبار (سيتم تشغيل تطبيق النظام)' } });
        results.push({ tool: 'share_file', result: { success: null, message: 'لم يتم الاختبار (سيتم فتح لوحة المشاركة)' } });
        complete({
            success: true,
            message: 'تم تحميل حزمة أدوات الملفات الموسّعة (لم تُنفّذ اختبارات مدمّرة)',
            data: { results }
        });
    }
    return {
        file_exists: (params) => wrapToolExecution(file_exists, params),
        move_file: (params) => wrapToolExecution(move_file, params),
        copy_file: (params) => wrapToolExecution(copy_file, params),
        file_info: (params) => wrapToolExecution(file_info, params),
        zip_files: (params) => wrapToolExecution(zip_files, params),
        unzip_files: (params) => wrapToolExecution(unzip_files, params),
        open_file: (params) => wrapToolExecution(open_file, params),
        share_file: (params) => wrapToolExecution(share_file, params),
        main,
    };
})();
exports.file_exists = ExtendedFileTools.file_exists;
exports.move_file = ExtendedFileTools.move_file;
exports.copy_file = ExtendedFileTools.copy_file;
exports.file_info = ExtendedFileTools.file_info;
exports.zip_files = ExtendedFileTools.zip_files;
exports.unzip_files = ExtendedFileTools.unzip_files;
exports.open_file = ExtendedFileTools.open_file;
exports.share_file = ExtendedFileTools.share_file;
exports.main = ExtendedFileTools.main;
