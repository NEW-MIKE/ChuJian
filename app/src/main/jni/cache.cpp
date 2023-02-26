#include "cache.h"
#include "misc.h"

Frame::Frame() : m_iFrameW(0), m_iFrameH(0), m_iFrameSize(0), m_pFrameData(nullptr) {
}

Frame::Frame(int ww, int hh) : m_iFrameW(ww), m_iFrameH(hh){
    changeSize(m_iFrameW, m_iFrameH);
    m_pFrameData = (unsigned char *) malloc(m_iFrameSize);
}

Frame::Frame(const Frame &other) {
    changeSize(other.m_iFrameW, other.m_iFrameH);
    memcpy(m_pFrameData, other.m_pFrameData, m_iFrameSize);
}

Frame::Frame(Frame &&that) noexcept {
    m_iFrameW = that.m_iFrameW;
    m_iFrameH = that.m_iFrameH;
    m_pFrameData = that.m_pFrameData;
    that.m_pFrameData = nullptr;
    that.m_iFrameW = 0;
    that.m_iFrameH = 0;
}

Frame::~Frame() {
    clear();
}

Frame &Frame::operator=(Frame &&that) {
    clear();
    m_iFrameW = that.m_iFrameW;
    m_iFrameH = that.m_iFrameH;
    m_pFrameData = that.m_pFrameData;
    that.m_pFrameData = nullptr;
    that.m_iFrameW = 0;
    that.m_iFrameH = 0;
    return *this;
}

Frame &Frame::operator=(const Frame &other) {
    changeSize(other.m_iFrameW, other.m_iFrameH);
    memcpy(m_pFrameData, other.m_pFrameData, m_iFrameSize);
    return *this;
}

void Frame::clear() {
    if (m_pFrameData) {
        LOGE("free(m_pFrameData)");
        free(m_pFrameData);
    }
}

void Frame::changeSize(const int ww, const int hh) {
    if (m_iFrameW < ww || m_iFrameH < hh) {
        clear();
        m_iFrameSize = TDIBWIDTHBYTES(24 * ww) * hh;
        m_pFrameData = (unsigned char *) malloc(m_iFrameSize);
    }
    m_iFrameW = ww;
    m_iFrameH = hh;
}

void Frame::store(const unsigned char *v, const int &ww, const int &hh) {
    changeSize(ww, hh);
    if (v != nullptr)
        memcpy(m_pFrameData, v, m_iFrameSize);
}

void Cache::init() {
    m_queueProducer = new std::queue<Frame *>();
    m_queueConsumer = new std::queue<Frame *>();
    m_bWork = true;
    m_pThread = new std::thread(exchange, this);
    m_pThread->detach();
}

Cache::~Cache() {
    delete m_queueProducer;
    delete m_queueConsumer;
    m_queueProducer = m_queueConsumer = nullptr;
}

Cache::Cache() {
    Cache(5);
}

Cache::Cache(int capacity) : m_iCapacity(capacity) {
    init();
}

void Cache::clear(std::queue<Frame *> *q) {
    while (!q->empty()) {
        delete q->front();
        q->pop();
    }
}

void Cache::exchange(Cache *cache) {
    while (cache->m_bWork) {
        std::unique_lock<std::mutex> lockerEx(cache->m_mtxCs);
        cache->m_exchangeCv.wait(lockerEx);

        std::unique_lock<std::mutex> lockerPr(cache->m_mtxPd);
        if (cache->m_queueProducer->empty())
            cache->m_produceCv.wait(lockerPr);

        swap(cache->m_queueConsumer, cache->m_queueProducer);
        lockerEx.unlock();
        cache->m_exchangeCv.notify_one();
        cache->clear(cache->m_queueProducer);
    }
}

void Cache::produce(const unsigned char *data, const int &m_iFrameW, const int &m_iFrameH) {
    if (!m_bWork)
        return;
    auto *frame = new Frame();
    frame->store(data, m_iFrameW, m_iFrameH);
    m_mtxPd.lock();
    m_queueProducer->push(frame);
    if (m_queueProducer->size() >= m_iCapacity) {
        m_exchangeCv.notify_one();
    }
    m_mtxPd.unlock();
    m_produceCv.notify_one();
}

Frame *Cache::consume() {
    if (!m_bWork)
        return nullptr;
    std::unique_lock<std::mutex> locker(m_mtxCs);
    while (m_queueConsumer->empty()) {
        m_exchangeCv.notify_one();
        if (m_bWork)
            m_exchangeCv.wait(locker);
        else
            return nullptr;
    }
    Frame *r = m_queueConsumer->front();
    m_queueConsumer->pop();
    return r;
}

void Cache::close() {
    m_bWork = false;
    m_exchangeCv.notify_all();
    m_produceCv.notify_all();
    std::unique_lock<std::mutex> locker1(m_mtxCs);
    std::unique_lock<std::mutex> locker2(m_mtxPd);
    clear(m_queueProducer);
    clear(m_queueConsumer);
}