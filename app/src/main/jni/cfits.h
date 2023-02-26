#ifndef CFITS_H
#define CFITS_H

class CFits
{
public:
    ~CFits() {}
    CFits(CFits&) = delete;
    CFits& operator = (const CFits&) = delete;
    static CFits& io()
    {
        static CFits sharedInstance;
        return sharedInstance;
    }
    bool write(const char* filePath, int width, int height, void* data, int ch, int bitpix);
    void* read(const char* filePath, int* width, int* height, int* ch, int* bitpix);
private:
    CFits() {}
    int type2bitpix(const int& type);
    int bitpix2type(const int& bitpix);
    template<typename T>
    void split(T* src, T** dst, int len, int cn)
    {
        switch (cn)
        {
        case 1:
        {
            for(int i = 0, j = 0; i < len; ++i, j += cn)
                dst[0][i] = src[j];
        }
            break;
        case 2:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
            {
                dst[0][i] = src[j];
                dst[1][i] = src[j+1];
            }
        }
            break;
        case 3:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
            {
                dst[0][i] = src[j];
                dst[1][i] = src[j+1];
                dst[2][i] = src[j+2];
            }
        }
            break;
        case 4:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
            {
                dst[0][i] = src[j];
                dst[1][i] = src[j+1];
                dst[2][i] = src[j+2];
                dst[3][i] = src[j+3];
            }
        }
            break;
        default:
            break;
        }
    }

    template<typename T>
    void merge(T** src, T* dst, int len, int cn)
    {
        switch (cn)
        {
        case 1:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
                dst[j] = src[0][i];
        }
            break;
        case 2:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
            {
                dst[j] = src[0][i];
                dst[j+1] = src[1][i];
            }
        }
            break;
        case 3:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
            {
                dst[j] = src[0][i];
                dst[j+1] = src[1][i];
                dst[j+2] = src[2][i];
            }
        }
            break;
        case 4:
        {
            for (int i = 0, j = 0; i < len; ++i, j += cn)
            {
                dst[j] = src[0][i];
                dst[j+1] = src[1][i];
                dst[j+2] = src[2][i];
                dst[j+3] = src[3][i];
            }
        }
            break;
        default:
            break;
        }
    }
};

#endif /* CFITS_H */
