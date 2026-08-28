-- ==============================================================================
-- TÂLIB (طالب) - Comprehensive Production Database Schema & Security Policies
-- Compatible with both Web (talib_app.html, talib_admin.html) and Android App
-- ==============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ------------------------------------------------------------------------------
-- 1. SPECIALTIES (التخصصات الأكاديمية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.specialties (
    id BIGSERIAL PRIMARY KEY,
    name_ar TEXT NOT NULL,
    code TEXT NOT NULL UNIQUE,
    icon_name TEXT DEFAULT 'book',
    description TEXT DEFAULT '',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 2. ACADEMIC YEARS (السنوات الدراسية والسداسيات)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.academic_years (
    id BIGSERIAL PRIMARY KEY,
    specialty_id BIGINT NOT NULL REFERENCES public.specialties(id) ON DELETE CASCADE,
    year_name TEXT NOT NULL,
    semester INT DEFAULT 1,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 3. PROFILES (ملفات الطلاب والمشرفين والمندوبين)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL DEFAULT '',
    student_id TEXT DEFAULT '',
    institution TEXT DEFAULT '',
    university TEXT DEFAULT '',
    faculty TEXT DEFAULT '',
    specialty_id BIGINT REFERENCES public.specialties(id) ON DELETE SET NULL,
    specialty_name TEXT DEFAULT '',
    profile_track TEXT DEFAULT '',
    academic_year_id BIGINT REFERENCES public.academic_years(id) ON DELETE SET NULL,
    academic_year_name TEXT DEFAULT '',
    semester_name TEXT DEFAULT 'السداسي الأول',
    group_number TEXT DEFAULT 'الفوج 01',
    sub_group TEXT DEFAULT 'الفوج الفرعي A',
    email TEXT,
    role TEXT NOT NULL DEFAULT 'STUDENT' CHECK (role IN ('STUDENT', 'REPRESENTATIVE', 'SPECIALTY_ADMIN', 'OWNER')),
    theme_palette TEXT DEFAULT 'ACADEMIC',
    is_configured BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 4. MODULES (المقاييس والمقررات الدراسية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.modules (
    id BIGSERIAL PRIMARY KEY,
    specialty_id BIGINT NOT NULL REFERENCES public.specialties(id) ON DELETE CASCADE,
    academic_year_id BIGINT NOT NULL REFERENCES public.academic_years(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    code TEXT NOT NULL,
    coefficient NUMERIC(4,2) DEFAULT 2.0,
    credits INT DEFAULT 4,
    professor_name TEXT DEFAULT '',
    professor_email TEXT DEFAULT '',
    category TEXT DEFAULT 'أساسي', -- 'أساسي' / 'منهجي' / 'استكشافي'
    description TEXT DEFAULT '',
    syllabus_topics TEXT DEFAULT '',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 5. LECTURES (المحاضرات والملفات الرقمية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.lectures (
    id BIGSERIAL PRIMARY KEY,
    module_id BIGINT NOT NULL REFERENCES public.modules(id) ON DELETE CASCADE,
    week_number INT NOT NULL DEFAULT 1,
    title TEXT NOT NULL,
    summary TEXT DEFAULT '',
    pdf_file_name TEXT DEFAULT '',
    pdf_url TEXT DEFAULT '',
    duration_minutes INT DEFAULT 90,
    date TEXT DEFAULT '',
    cached_content_text TEXT DEFAULT '',
    visibility_scope TEXT NOT NULL DEFAULT 'تخصص كامل' CHECK (visibility_scope IN ('تخصص كامل', 'عدة أفواج محددة', 'فوج واحد')),
    target_groups TEXT DEFAULT 'الكل',
    uploaded_by UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 6. CACHED COURSE MATERIALS (المراجع والملخصات الرقمية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.cached_materials (
    id BIGSERIAL PRIMARY KEY,
    module_id BIGINT NOT NULL REFERENCES public.modules(id) ON DELETE CASCADE,
    module_name TEXT NOT NULL,
    title TEXT NOT NULL,
    material_type TEXT DEFAULT 'محاضرة' CHECK (material_type IN ('محاضرة', 'ملخص دراسي', 'أعمال موجهة TD', 'امتحان سابق')),
    summary TEXT DEFAULT '',
    full_text TEXT DEFAULT '',
    key_concepts TEXT DEFAULT '',
    week_number INT DEFAULT 1,
    file_url TEXT DEFAULT '',
    visibility_scope TEXT DEFAULT 'تخصص كامل',
    target_groups TEXT DEFAULT 'الكل',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 7. ASSIGNMENTS (الواجبات والمهام الدراسية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.assignments (
    id BIGSERIAL PRIMARY KEY,
    module_id BIGINT NOT NULL REFERENCES public.modules(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    due_date TEXT NOT NULL,
    description TEXT DEFAULT '',
    max_score NUMERIC(4,2) DEFAULT 20.0,
    visibility_scope TEXT DEFAULT 'تخصص كامل',
    target_groups TEXT DEFAULT 'الكل',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 8. SCHEDULES (التوقيت وجدول الحصص الأسبوعي)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.schedules (
    id BIGSERIAL PRIMARY KEY,
    specialty_id BIGINT NOT NULL REFERENCES public.specialties(id) ON DELETE CASCADE,
    academic_year_id BIGINT NOT NULL REFERENCES public.academic_years(id) ON DELETE CASCADE,
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7), -- 1: الأحد, 2: الإثنين, 3: الثلاثاء, 4: الأربعاء, 5: الخميس
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    module_name TEXT NOT NULL,
    type TEXT DEFAULT 'محاضرة', -- 'محاضرة' / 'أعمال موجهة TD' / 'أعمال تطبيقية TP'
    room TEXT DEFAULT '',
    professor TEXT DEFAULT '',
    group_number TEXT DEFAULT 'الكل',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 9. EXAMS (جدول الاختبارات والامتحانات)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.exams (
    id BIGSERIAL PRIMARY KEY,
    module_id BIGINT NOT NULL REFERENCES public.modules(id) ON DELETE CASCADE,
    module_name TEXT NOT NULL,
    title TEXT NOT NULL,
    exam_date TEXT NOT NULL,
    time TEXT NOT NULL,
    room TEXT DEFAULT '',
    coefficient NUMERIC(4,2) DEFAULT 2.0,
    visibility_scope TEXT DEFAULT 'تخصص كامل',
    target_groups TEXT DEFAULT 'الكل',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 10. GRADES (العلامات وكشوف النقاط والمعدل)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.grades (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    module_id BIGINT NOT NULL REFERENCES public.modules(id) ON DELETE CASCADE,
    module_name TEXT NOT NULL,
    continuous_score NUMERIC(4,2) DEFAULT 0.0,
    exam_score NUMERIC(4,2) DEFAULT 0.0,
    coefficient NUMERIC(4,2) DEFAULT 2.0,
    credits INT DEFAULT 4,
    is_official BOOLEAN DEFAULT FALSE,
    target_score NUMERIC(4,2) DEFAULT 10.0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 11. ANNOUNCEMENTS (لوحة التنبيهات والإعلانات الرسمية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.announcements (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    author TEXT NOT NULL,
    date TEXT NOT NULL,
    urgency TEXT DEFAULT 'عام' CHECK (urgency IN ('عام', 'هام', 'عاجل')),
    specialty_id BIGINT REFERENCES public.specialties(id) ON DELETE CASCADE,
    visibility_scope TEXT DEFAULT 'تخصص كامل' CHECK (visibility_scope IN ('تخصص كامل', 'عدة أفواج محددة', 'فوج واحد')),
    target_groups TEXT DEFAULT 'الكل',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------------
-- 12. STUDENT NOTES (ملفاتي والملاحظات الشخصية)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.student_notes (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    module_name TEXT DEFAULT 'عام',
    color_hex TEXT DEFAULT '#1B5E4B',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ==============================================================================
-- STORAGE SETUP (Supabase Storage Bucket: talib-docs)
-- ==============================================================================
INSERT INTO storage.buckets (id, name, public)
VALUES ('talib-docs', 'talib-docs', true)
ON CONFLICT (id) DO UPDATE SET public = true;

-- ==============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ==============================================================================

ALTER TABLE public.specialties ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.academic_years ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.modules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lectures ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cached_materials ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.assignments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.exams ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.grades ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.student_notes ENABLE ROW LEVEL SECURITY;

-- Helper function to get the current user role
CREATE OR REPLACE FUNCTION public.get_current_user_role()
RETURNS TEXT AS $$
  SELECT role FROM public.profiles WHERE id = auth.uid();
$$ LANGUAGE sql STABLE SECURITY DEFINER;

-- Helper function to get current user specialty
CREATE OR REPLACE FUNCTION public.get_current_user_specialty()
RETURNS BIGINT AS $$
  SELECT specialty_id FROM public.profiles WHERE id = auth.uid();
$$ LANGUAGE sql STABLE SECURITY DEFINER;

-- Helper function to get current user group
CREATE OR REPLACE FUNCTION public.get_current_user_group()
RETURNS TEXT AS $$
  SELECT group_number FROM public.profiles WHERE id = auth.uid();
$$ LANGUAGE sql STABLE SECURITY DEFINER;

-- 1. Profiles Policies
DROP POLICY IF EXISTS "Users can read profiles" ON public.profiles;
CREATE POLICY "Users can read profiles"
ON public.profiles FOR SELECT
TO authenticated
USING (true);

DROP POLICY IF EXISTS "Users can update own profile or admins manage" ON public.profiles;
CREATE POLICY "Users can update own profile or admins manage"
ON public.profiles FOR UPDATE
TO authenticated
USING (id = auth.uid() OR public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN'));

DROP POLICY IF EXISTS "Users can insert own profile" ON public.profiles;
CREATE POLICY "Users can insert own profile"
ON public.profiles FOR INSERT
TO authenticated
WITH CHECK (id = auth.uid() OR public.get_current_user_role() = 'OWNER');

-- 2. Specialties Policies
DROP POLICY IF EXISTS "Anyone authenticated can read specialties" ON public.specialties;
CREATE POLICY "Anyone authenticated can read specialties"
ON public.specialties FOR SELECT
TO authenticated, anon
USING (true);

DROP POLICY IF EXISTS "Admins can manage specialties" ON public.specialties;
CREATE POLICY "Admins can manage specialties"
ON public.specialties FOR ALL
TO authenticated
USING (public.get_current_user_role() = 'OWNER');

-- 3. Academic Years Policies
DROP POLICY IF EXISTS "Anyone can read academic years" ON public.academic_years;
CREATE POLICY "Anyone can read academic years"
ON public.academic_years FOR SELECT
TO authenticated, anon
USING (true);

DROP POLICY IF EXISTS "Admins can manage academic years" ON public.academic_years;
CREATE POLICY "Admins can manage academic years"
ON public.academic_years FOR ALL
TO authenticated
USING (public.get_current_user_role() = 'OWNER');

-- 4. Modules Policies
DROP POLICY IF EXISTS "Anyone authenticated can read modules" ON public.modules;
CREATE POLICY "Anyone authenticated can read modules"
ON public.modules FOR SELECT
TO authenticated, anon
USING (true);

DROP POLICY IF EXISTS "Staff can manage modules" ON public.modules;
CREATE POLICY "Staff can manage modules"
ON public.modules FOR ALL
TO authenticated
USING (public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN'));

-- 5. Lectures Policies (Scoped by Visibility & Specialty)
DROP POLICY IF EXISTS "Students read permitted lectures" ON public.lectures;
CREATE POLICY "Students read permitted lectures"
ON public.lectures FOR SELECT
TO authenticated, anon
USING (
    public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN', 'REPRESENTATIVE')
    OR visibility_scope = 'تخصص كامل'
    OR (visibility_scope = 'فوج واحد' AND (target_groups = public.get_current_user_group() OR target_groups = 'الكل'))
    OR (visibility_scope = 'عدة أفواج محددة' AND (target_groups LIKE '%' || public.get_current_user_group() || '%' OR target_groups = 'الكل'))
    OR auth.uid() IS NULL
);

DROP POLICY IF EXISTS "Staff can manage lectures" ON public.lectures;
CREATE POLICY "Staff can manage lectures"
ON public.lectures FOR ALL
TO authenticated
USING (public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN', 'REPRESENTATIVE'));

-- 6. Announcements Policies
DROP POLICY IF EXISTS "Students read permitted announcements" ON public.announcements;
CREATE POLICY "Students read permitted announcements"
ON public.announcements FOR SELECT
TO authenticated, anon
USING (
    public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN', 'REPRESENTATIVE')
    OR visibility_scope = 'تخصص كامل'
    OR (visibility_scope = 'فوج واحد' AND (target_groups = public.get_current_user_group() OR target_groups = 'الكل'))
    OR (visibility_scope = 'عدة أفواج محددة' AND (target_groups LIKE '%' || public.get_current_user_group() || '%' OR target_groups = 'الكل'))
    OR auth.uid() IS NULL
);

DROP POLICY IF EXISTS "Staff can manage announcements" ON public.announcements;
CREATE POLICY "Staff can manage announcements"
ON public.announcements FOR ALL
TO authenticated
USING (public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN', 'REPRESENTATIVE'));

-- 7. Assignments Policies
DROP POLICY IF EXISTS "Students read assignments" ON public.assignments;
CREATE POLICY "Students read assignments"
ON public.assignments FOR SELECT
TO authenticated, anon
USING (true);

DROP POLICY IF EXISTS "Staff can manage assignments" ON public.assignments;
CREATE POLICY "Staff can manage assignments"
ON public.assignments FOR ALL
TO authenticated
USING (public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN', 'REPRESENTATIVE'));

-- 8. Schedules Policies
DROP POLICY IF EXISTS "Students read schedules" ON public.schedules;
CREATE POLICY "Students read schedules"
ON public.schedules FOR SELECT
TO authenticated, anon
USING (true);

DROP POLICY IF EXISTS "Staff can manage schedules" ON public.schedules;
CREATE POLICY "Staff can manage schedules"
ON public.schedules FOR ALL
TO authenticated
USING (public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN'));

-- 9. Exams Policies
DROP POLICY IF EXISTS "Students read exams" ON public.exams;
CREATE POLICY "Students read exams"
ON public.exams FOR SELECT
TO authenticated, anon
USING (true);

DROP POLICY IF EXISTS "Staff can manage exams" ON public.exams;
CREATE POLICY "Staff can manage exams"
ON public.exams FOR ALL
TO authenticated
USING (public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN', 'REPRESENTATIVE'));

-- 10. Grades Policies
DROP POLICY IF EXISTS "Users can read own grades or staff" ON public.grades;
CREATE POLICY "Users can read own grades or staff"
ON public.grades FOR SELECT
TO authenticated
USING (user_id = auth.uid() OR public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN'));

DROP POLICY IF EXISTS "Users can manage own grades" ON public.grades;
CREATE POLICY "Users can manage own grades"
ON public.grades FOR ALL
TO authenticated
USING (user_id = auth.uid() OR public.get_current_user_role() IN ('OWNER', 'SPECIALTY_ADMIN'));

-- 11. Student Notes Policies
DROP POLICY IF EXISTS "Users can manage own notes" ON public.student_notes;
CREATE POLICY "Users can manage own notes"
ON public.student_notes FOR ALL
TO authenticated
USING (user_id = auth.uid())
WITH CHECK (user_id = auth.uid());

-- 12. Storage Policies for talib-docs bucket
DROP POLICY IF EXISTS "Public can view documents in talib-docs" ON storage.objects;
CREATE POLICY "Public can view documents in talib-docs"
ON storage.objects FOR SELECT
USING (bucket_id = 'talib-docs');

DROP POLICY IF EXISTS "Authenticated staff can upload documents to talib-docs" ON storage.objects;
CREATE POLICY "Authenticated staff can upload documents to talib-docs"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'talib-docs');

DROP POLICY IF EXISTS "Authenticated staff can delete documents in talib-docs" ON storage.objects;
CREATE POLICY "Authenticated staff can delete documents in talib-docs"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'talib-docs');

-- ------------------------------------------------------------------------------
-- AUTO PROFILE CREATION TRIGGER ON AUTH SIGNUP
-- ------------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (
        id,
        full_name,
        email,
        role,
        group_number,
        sub_group,
        theme_palette
    )
    VALUES (
        new.id,
        COALESCE(new.raw_user_meta_data->>'full_name', 'طالب جديد'),
        new.email,
        COALESCE(new.raw_user_meta_data->>'role', 'STUDENT'),
        COALESCE(new.raw_user_meta_data->>'group_number', 'الفوج 01'),
        COALESCE(new.raw_user_meta_data->>'sub_group', 'الفوج الفرعي A'),
        COALESCE(new.raw_user_meta_data->>'theme_palette', 'ACADEMIC')
    )
    ON CONFLICT (id) DO NOTHING;
    RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
AFTER INSERT ON auth.users
FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();
