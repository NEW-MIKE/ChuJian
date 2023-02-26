#ifndef __cache_h__
#define __cache_h__

#include <thread>
#include <queue>
#include <mutex>
#include <condition_variable>

class Frame {
    size_t m_iFrameSize;
    void clear();
public:
    int m_iFrameW;
    int m_iFrameH;
    unsigned char *m_pFrameData;
    Frame();
    Frame(int ww, int hh);
    Frame(const Frame &other);
    Frame(Frame &&that) noexcept;
    ~Frame();
    Frame &operator=(const Frame &other);
    Frame &operator=(Frame &&that);
    void changeSize(const int ww, const int hh);
    inline size_t getSize() const {return m_iFrameSize;}
    void store(const unsigned char *v, const int &ww, const int &hh);
};

template<class T>
class CircleQueue {
private:
    std::atomic_char front, rear;
    unsigned capacity;
    T *data;
    void init() {
        front = rear = 0;
        if (capacity < 2)
            capacity = 2;
        data = new T[capacity];
    }
    void circle_inc(std::atomic_char &n) {
        n = (n + 1) % capacity;
    }
    void judge_enqueue() {
        circle_inc(front);
        if (empty()) {
            circle_inc(rear);
        }
    }
public:
    CircleQueue(int capa) : capacity(capa) {
        init();
    }
    CircleQueue() : capacity(5) {
        init();
    }
    ~CircleQueue() {
        delete[] data;
    }
    bool empty() {
        return front == rear;
    }
    void emplace(T &t) {
        data[front] = move(t);
        judge_enqueue();
    }
    void enqueue(std::function<void(T *)> callback) {
        callback(&data[front]);
        judge_enqueue();
    }
    void enqueue(T &t) {
        data[front] = t;
        judge_enqueue();
    }
    T *dequeue() {
        T *r = nullptr;
        if (!empty()) {
            r = &data[rear];
            circle_inc(rear);
        }
        return r;
    }
    T *get_rear() {
        T *r = nullptr;
        if (!empty()) {
            r = &data[rear];
        }
        return r;
    }
    void get_rear(T &t) { //拷贝
        t = data[rear];
    }
    void clear() {
        rear = 0;
        front = 0;
    }
};

class Cache {
private:
    int m_iCapacity;
    std::queue<Frame *> *m_queueProducer;
    std::queue<Frame *> *m_queueConsumer;
    std::thread *m_pThread;
    std::mutex m_mtxPd;
    std::mutex m_mtxCs;
    std::condition_variable m_exchangeCv;
    std::condition_variable m_produceCv;
    std::atomic_bool m_bWork;
    void init();
    void clear(std::queue<Frame *> *);
    static void exchange(Cache *cache);
public:
    ~Cache();
    Cache();
    Cache(int capacity);
    void produce(const unsigned char *data, const int &w, const int &h);
    Frame *consume();
    void close();
};

#endif